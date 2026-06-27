/* global jQuery, bootstrap */
(function ($) {
    'use strict';

    var PAGE_TITLES = {
        dashboard: '売上統計',
        shipping: '出荷処理',
        products: '商品一覧',
        'product-form': '商品登録/更新',
        users: 'ユーザー一覧',
        'user-form': 'ユーザー登録/更新',
        orders: '最近の注文履歴'
    };

    var ORDER_STATUS_LABELS = {
        PENDING: '処理待ち',
        PAID: '支払済',
        SHIPPING: '配送中',
        COMPLETED: '完了',
        CANCELLED: 'キャンセル'
    };

    var currentShipStatus = 'PAID';
    var selectedOrderId = null;
    var shipModal = null;
    var gridInitialized = false;
    var NAV_MODE_KEY = 'wms-nav-mode';

    function resizeShippingGrid() {
        if (gridInitialized && $('#ordersGrid').length) {
            var w = $('#ordersGrid').closest('.card-body').width();
            if (w) {
                $('#ordersGrid').jqGrid('setGridWidth', w, true);
            }
        }
    }

    function applyNavMode(mode) {
        if (mode !== 'header' && mode !== 'sidebar') {
            mode = 'header';
        }
        $('#app').removeClass('nav-mode-header nav-mode-sidebar').addClass('nav-mode-' + mode);
        $('.nav-mode-btn').removeClass('active');
        $('.nav-mode-btn[data-nav-mode="' + mode + '"]').addClass('active');
        try {
            localStorage.setItem(NAV_MODE_KEY, mode);
        } catch (e) {
            // ignore
        }
        setTimeout(resizeShippingGrid, 100);
    }

    function showToast(message, type) {
        var $toast = $('#toast');
        $toast.removeClass('d-none alert-success alert-danger alert-info')
            .addClass('alert alert-' + (type === 'error' ? 'danger' : 'success'))
            .text(message);
        setTimeout(function () { $toast.addClass('d-none'); }, 3500);
    }

    function apiRequest(options) {
        return $.ajax($.extend({
            dataType: 'json',
            contentType: 'application/json'
        }, options)).fail(function (xhr) {
            var msg = xhr.responseJSON && xhr.responseJSON.message
                ? xhr.responseJSON.message
                : 'リクエストに失敗しました';
            showToast(msg, 'error');
        });
    }

    function formatMoney(value) {
        var num = Number(value || 0);
        return '¥' + num.toLocaleString('ja-JP', { minimumFractionDigits: 0, maximumFractionDigits: 0 });
    }

    function formatDate(value) {
        if (!value) return '-';
        return String(value).replace('T', ' ').substring(0, 19);
    }

    function resolveImageUrl(url) {
        if (!url) return '';
        if (url.startsWith('http') || url.startsWith('/files/')) return url;
        return url;
    }

    function escapeHtml(text) {
        return $('<div/>').text(text || '').html();
    }

    function navigate(page) {
        $('.app-nav .nav-item').removeClass('active');
        $('.app-nav .nav-item[data-page="' + page + '"]').addClass('active');
        $('.page-panel').removeClass('active');
        $('#page-' + page).addClass('active');
        $('#page-title').text(PAGE_TITLES[page] || 'ショップ管理');

        if (page === 'dashboard') loadDashboard();
        if (page === 'shipping') initShippingGrid();
        if (page === 'products') loadProducts();
        if (page === 'users') loadUsers();
        if (page === 'orders') loadOrders();
    }

    /* ---- Dashboard ---- */
    function loadDashboard() {
        apiRequest({ url: '/api/orders/stats/summary', method: 'GET' }).done(function (stats) {
            $('#stat-total-sales').text(formatMoney(stats.totalSalesAmount));
            $('#stat-total-orders').text(stats.totalOrders);
            $('#stat-completed-orders').text(stats.completedOrders);
            $('#stat-active-products').text(stats.activeProducts);
            $('#stat-active-users').text(stats.activeUsers);
        });
    }

    /* ---- Shipping (jqGrid) ---- */
    function shippingGridUrl() {
        return '/api/wms/orders/grid?status=' + encodeURIComponent(currentShipStatus);
    }

    function actionFormatter(cellValue, options, rowObject) {
        if (currentShipStatus !== 'PAID') return '';
        var orderNumber = rowObject && rowObject.cell ? rowObject.cell[0] : '';
        return '<button type="button" class="btn btn-sm btn-primary ship-action-btn" ' +
            'data-id="' + options.rowId + '" data-order-number="' + escapeHtml(orderNumber) + '">出荷する</button>';
    }

    function initShippingGrid() {
        if (!gridInitialized) {
            $.jgrid.defaults.styleUI = 'Bootstrap5';
            $('#ordersGrid').jqGrid({
                url: shippingGridUrl(),
                datatype: 'json',
                mtype: 'GET',
                colNames: ['注文番号', '合計', 'ユーザーID', 'ステータス', '送り場所', '商品明細', '作成日時', '操作'],
                colModel: [
                    { name: 'orderNumber', width: 130, sortable: false },
                    { name: 'totalAmount', width: 90, align: 'right', sortable: false },
                    { name: 'userId', width: 80, align: 'center', sortable: false },
                    { name: 'statusLabel', width: 90, align: 'center', sortable: false },
                    { name: 'shippingAddress', width: 160, sortable: false },
                    { name: 'itemsSummary', width: 200, sortable: false },
                    { name: 'createdAt', width: 120, sortable: false },
                    { name: 'actions', width: 90, align: 'center', sortable: false, fixed: true, formatter: actionFormatter }
                ],
                jsonReader: { root: 'rows', page: 'page', total: 'total', records: 'records', repeatitems: true, id: 'id' },
                rowNum: 20,
                rowList: [10, 20, 50],
                pager: '#ordersPager',
                viewrecords: true,
                height: 'auto',
                autowidth: true,
                shrinkToFit: true,
                gridComplete: function () {
                    if (currentShipStatus !== 'PAID') {
                        $('#ordersGrid').hideCol('actions');
                    } else {
                        $('#ordersGrid').showCol('actions');
                    }
                }
            });
            gridInitialized = true;
        } else {
            reloadShippingGrid();
        }
    }

    function reloadShippingGrid() {
        $('#ordersGrid').jqGrid('setGridParam', { url: shippingGridUrl(), page: 1 }).trigger('reloadGrid');
    }

    function openShipModal(orderId, orderNumber) {
        selectedOrderId = orderId;
        $('#shipOrderNumber').text(orderNumber);
        $('#shippingAddress').val('');
        $('#shipModalError').addClass('d-none').text('');
        shipModal.show();
    }

    function confirmShip() {
        var address = $('#shippingAddress').val().trim();
        if (!address) {
            $('#shipModalError').removeClass('d-none').text('送り場所を入力してください');
            return;
        }
        var $btn = $('#btnConfirmShip');
        $btn.prop('disabled', true).text('処理中...');
        $.ajax({
            url: '/api/wms/orders/' + selectedOrderId + '/ship',
            method: 'POST',
            contentType: 'application/json',
            data: JSON.stringify({ shippingAddress: address }),
            success: function () {
                shipModal.hide();
                showToast('出荷処理が完了しました', 'success');
                reloadShippingGrid();
            },
            error: function (xhr) {
                var msg = '出荷処理に失敗しました';
                if (xhr.responseJSON && xhr.responseJSON.message) msg = xhr.responseJSON.message;
                $('#shipModalError').removeClass('d-none').text(msg);
            },
            complete: function () {
                $btn.prop('disabled', false).text('出荷済みにする');
            }
        });
    }

    /* ---- Products ---- */
    function loadProducts() {
        apiRequest({
            url: '/api/products',
            method: 'GET',
            data: {
                keyword: $('#product-search').val(),
                includeInactive: $('#product-include-inactive').is(':checked')
            }
        }).done(function (items) {
            var rows = items.map(function (p) {
                var img = resolveImageUrl(p.imageUrl);
                return '<tr>' +
                    '<td>' + p.id + '</td>' +
                    '<td>' + (img ? '<img class="img-thumb" src="' + img + '" alt="">' : '-') + '</td>' +
                    '<td>' + escapeHtml(p.name) + '</td>' +
                    '<td>' + escapeHtml(p.category || '-') + '</td>' +
                    '<td>' + formatMoney(p.price) + '</td>' +
                    '<td>' + (p.stock != null ? p.stock : '-') + '</td>' +
                    '<td><button class="btn btn-link btn-sm p-0 edit-product" data-id="' + p.id + '">編集</button> ' +
                    '<button class="btn btn-link btn-sm p-0 text-danger deactivate-product" data-id="' + p.id + '">無効化</button></td>' +
                    '</tr>';
            }).join('');
            $('#product-table-body').html(rows || '<tr><td colspan="7" class="text-center text-muted">データがありません</td></tr>');
        });
    }

    function resetProductForm() {
        $('#product-form')[0].reset();
        $('#product-id').val('');
        $('#product-image-preview').attr('src', '').hide();
    }

    function fillProductForm(product) {
        $('#product-id').val(product.id);
        $('#product-name').val(product.name);
        $('#product-category').val(product.category || '');
        $('#product-price').val(product.price);
        $('#product-stock').val(product.stock);
        $('#product-description').val(product.description || '');
        $('#product-image-url').val(product.imageUrl || '');
        var img = resolveImageUrl(product.imageUrl);
        if (img) { $('#product-image-preview').attr('src', img).show(); }
        else { $('#product-image-preview').attr('src', '').hide(); }
    }

    /* ---- Users ---- */
    function loadUsers() {
        apiRequest({
            url: '/api/users',
            method: 'GET',
            data: {
                keyword: $('#user-search').val(),
                includeInactive: $('#user-include-inactive').is(':checked')
            }
        }).done(function (items) {
            var rows = items.map(function (u) {
                return '<tr>' +
                    '<td>' + u.id + '</td>' +
                    '<td>' + escapeHtml(u.username) + '</td>' +
                    '<td>' + escapeHtml(u.email) + '</td>' +
                    '<td>' + u.role + '</td>' +
                    '<td><button class="btn btn-link btn-sm p-0 edit-user" data-id="' + u.id + '">編集</button> ' +
                    '<button class="btn btn-link btn-sm p-0 text-danger deactivate-user" data-id="' + u.id + '">無効化</button></td>' +
                    '</tr>';
            }).join('');
            $('#user-table-body').html(rows || '<tr><td colspan="5" class="text-center text-muted">データがありません</td></tr>');
        });
    }

    function resetUserForm() {
        $('#user-form')[0].reset();
        $('#user-id').val('');
        $('#user-role').val('VIEWER');
        $('#user-avatar-preview').attr('src', '').hide();
    }

    function fillUserForm(user) {
        $('#user-id').val(user.id);
        $('#user-username').val(user.username);
        $('#user-email').val(user.email);
        $('#user-role').val(user.role);
        $('#user-password').val('');
        $('#user-avatar-url').val(user.avatarUrl || '');
        var img = resolveImageUrl(user.avatarUrl);
        if (img) { $('#user-avatar-preview').attr('src', img).show(); }
        else { $('#user-avatar-preview').attr('src', '').hide(); }
    }

    /* ---- Orders ---- */
    function loadOrders() {
        apiRequest({ url: '/api/orders/recent', method: 'GET', data: { limit: 50 } }).done(function (items) {
            var rows = items.map(function (o) {
                return '<tr>' +
                    '<td>' + escapeHtml(o.orderNumber) + '</td>' +
                    '<td>' + escapeHtml(o.username || ('#' + o.userId)) + '</td>' +
                    '<td>' + formatMoney(o.totalAmount) + '</td>' +
                    '<td>' + (ORDER_STATUS_LABELS[o.status] || o.status) + '</td>' +
                    '<td>' + formatDate(o.createdAt) + '</td>' +
                    '</tr>';
            }).join('');
            $('#order-table-body').html(rows || '<tr><td colspan="5" class="text-center text-muted">注文がありません</td></tr>');
        });
    }

    function uploadImage(fileInput, type, urlInput, previewImg) {
        var file = fileInput.files[0];
        if (!file) return $.Deferred().reject().promise();
        var formData = new FormData();
        formData.append('file', file);
        return $.ajax({
            url: '/api/files/upload?type=' + type,
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false
        }).done(function (res) {
            urlInput.val(res.url);
            previewImg.attr('src', res.url).show();
            showToast('画像をアップロードしました', 'success');
        }).fail(function (xhr) {
            var msg = xhr.responseJSON && xhr.responseJSON.message
                ? xhr.responseJSON.message : '画像のアップロードに失敗しました';
            showToast(msg, 'error');
        });
    }

    $(function () {
        shipModal = new bootstrap.Modal(document.getElementById('shipModal'));

        var savedNavMode = 'header';
        try {
            savedNavMode = localStorage.getItem(NAV_MODE_KEY) || 'header';
        } catch (e) {
            // ignore
        }
        applyNavMode(savedNavMode);

        $('.nav-mode-btn').on('click', function () {
            applyNavMode($(this).data('nav-mode'));
        });

        $(document).on('click', '.app-nav .nav-item', function () {
            navigate($(this).data('page'));
        });

        $('#shippingTabs').on('click', '.nav-link', function () {
            currentShipStatus = $(this).data('ship-status');
            $('#shippingTabs .nav-link').removeClass('active');
            $(this).addClass('active');
            reloadShippingGrid();
        });

        $(document).on('click', '.ship-action-btn', function () {
            openShipModal($(this).data('id'), $(this).data('order-number'));
        });
        $('#btnConfirmShip').on('click', confirmShip);

        $('#product-search-btn').on('click', loadProducts);
        $('#user-search-btn').on('click', loadUsers);
        $('#product-new-btn').on('click', function () { resetProductForm(); navigate('product-form'); });
        $('#user-new-btn').on('click', function () { resetUserForm(); navigate('user-form'); });
        $('#product-reset-btn').on('click', resetProductForm);
        $('#user-reset-btn').on('click', resetUserForm);

        $('#product-image-file').on('change', function () {
            uploadImage(this, 'product', $('#product-image-url'), $('#product-image-preview'));
        });
        $('#user-avatar-file').on('change', function () {
            uploadImage(this, 'user', $('#user-avatar-url'), $('#user-avatar-preview'));
        });

        $('#product-form').on('submit', function (e) {
            e.preventDefault();
            var id = $('#product-id').val();
            var payload = {
                name: $('#product-name').val(),
                category: $('#product-category').val(),
                price: Number($('#product-price').val()),
                stock: Number($('#product-stock').val()),
                description: $('#product-description').val(),
                imageUrl: $('#product-image-url').val(),
                active: true
            };
            apiRequest({
                url: id ? '/api/products/' + id : '/api/products',
                method: id ? 'PUT' : 'POST',
                data: JSON.stringify(payload)
            }).done(function () {
                showToast('商品を保存しました', 'success');
                navigate('products');
            });
        });

        $('#user-form').on('submit', function (e) {
            e.preventDefault();
            var id = $('#user-id').val();
            var payload = {
                username: $('#user-username').val(),
                email: $('#user-email').val(),
                role: $('#user-role').val(),
                password: $('#user-password').val(),
                avatarUrl: $('#user-avatar-url').val(),
                active: true
            };
            apiRequest({
                url: id ? '/api/users/' + id : '/api/users',
                method: id ? 'PUT' : 'POST',
                data: JSON.stringify(payload)
            }).done(function () {
                showToast('ユーザーを保存しました', 'success');
                navigate('users');
            });
        });

        $(document).on('click', '.edit-product', function () {
            apiRequest({ url: '/api/products/' + $(this).data('id'), method: 'GET' }).done(function (p) {
                fillProductForm(p);
                navigate('product-form');
            });
        });
        $(document).on('click', '.deactivate-product', function () {
            if (!confirm('この商品を無効化しますか？')) return;
            apiRequest({ url: '/api/products/' + $(this).data('id') + '/deactivate', method: 'POST' }).done(function () {
                showToast('商品を無効化しました', 'success');
                loadProducts();
            });
        });
        $(document).on('click', '.edit-user', function () {
            apiRequest({ url: '/api/users/' + $(this).data('id'), method: 'GET' }).done(function (u) {
                fillUserForm(u);
                navigate('user-form');
            });
        });
        $(document).on('click', '.deactivate-user', function () {
            if (!confirm('このユーザーを無効化しますか？')) return;
            apiRequest({ url: '/api/users/' + $(this).data('id') + '/deactivate', method: 'POST' }).done(function () {
                showToast('ユーザーを無効化しました', 'success');
                loadUsers();
            });
        });

        $(window).on('resize', resizeShippingGrid);

        navigate('dashboard');
    });
}(jQuery));
