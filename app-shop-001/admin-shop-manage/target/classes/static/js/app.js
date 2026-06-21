(function ($) {
    'use strict';

    const PAGE_TITLES = {
        dashboard: '売上統計',
        products: '商品一覧',
        'product-form': '商品登録/更新',
        users: 'ユーザー一覧',
        'user-form': 'ユーザー登録/更新',
        orders: '最近の注文履歴'
    };

    const ORDER_STATUS_LABELS = {
        PENDING: '保留中',
        PAID: '支払済',
        SHIPPING: '配送中',
        COMPLETED: '完了',
        CANCELLED: 'キャンセル'
    };

    function showToast(message, type) {
        const $toast = $('#toast');
        $toast.removeClass('hidden error success').addClass(type || 'success').text(message);
        setTimeout(function () { $toast.addClass('hidden'); }, 3000);
    }

    function apiRequest(options) {
        return $.ajax($.extend({
            dataType: 'json',
            contentType: 'application/json'
        }, options)).fail(function (xhr) {
            const msg = xhr.responseJSON && xhr.responseJSON.message
                ? xhr.responseJSON.message
                : 'リクエストに失敗しました';
            showToast(msg, 'error');
        });
    }

    function formatMoney(value) {
        const num = Number(value || 0);
        return '¥' + num.toLocaleString('ja-JP', { minimumFractionDigits: 2, maximumFractionDigits: 2 });
    }

    function formatDate(value) {
        if (!value) return '-';
        return String(value).replace('T', ' ').substring(0, 19);
    }

    function resolveImageUrl(url) {
        if (!url) return '';
        if (url.startsWith('http') || url.startsWith('/files/')) return url;
        if (url.startsWith('/api/files/')) return url.replace('/api/files/', '/files/');
        return url;
    }

    function navigate(page) {
        $('.nav-item').removeClass('active');
        $('.nav-item[data-page="' + page + '"]').addClass('active');
        $('.page').removeClass('active');
        $('#page-' + page).addClass('active');
        $('#page-title').text(PAGE_TITLES[page] || 'ショップ管理画面');

        if (page === 'dashboard') loadDashboard();
        if (page === 'products') loadProducts();
        if (page === 'users') loadUsers();
        if (page === 'orders') loadOrders();
    }

    function loadDashboard() {
        apiRequest({ url: '/api/orders/stats/summary', method: 'GET' }).done(function (stats) {
            $('#stat-total-sales').text(formatMoney(stats.totalSalesAmount));
            $('#stat-total-orders').text(stats.totalOrders);
            $('#stat-completed-orders').text(stats.completedOrders);
            $('#stat-active-products').text(stats.activeProducts);
            $('#stat-active-users').text(stats.activeUsers);
        });
    }

    function loadProducts() {
        const keyword = $('#product-search').val();
        const includeInactive = $('#product-include-inactive').is(':checked');
        apiRequest({
            url: '/api/products',
            method: 'GET',
            data: { keyword: keyword, includeInactive: includeInactive }
        }).done(function (items) {
            const rows = items.map(function (p) {
                const img = resolveImageUrl(p.imageUrl);
                const isActive = p.active !== false;
                return '<tr>' +
                    '<td>' + p.id + '</td>' +
                    '<td>' + (img ? '<img class="thumb" src="' + img + '" alt="">' : '-') + '</td>' +
                    '<td>' + escapeHtml(p.name) + '</td>' +
                    '<td>' + escapeHtml(p.category || '-') + '</td>' +
                    '<td>' + formatMoney(p.price) + '</td>' +
                    '<td>' + (p.stock ?? '-') + '</td>' +
                    '<td><span class="badge ' + (isActive ? 'badge-active' : 'badge-inactive') + '">' +
                    (isActive ? '有効' : '無効') + '</span></td>' +
                    '<td>' +
                    '<button class="btn-link edit-product" data-id="' + p.id + '">編集</button> ' +
                    (isActive ? '<button class="btn-link deactivate-product" data-id="' + p.id + '">無効化</button>' : '') +
                    '</td></tr>';
            }).join('');
            $('#product-table-body').html(rows || '<tr><td colspan="8">データがありません</td></tr>');
        });
    }

    function resetProductForm() {
        $('#product-form')[0].reset();
        $('#product-id').val('');
        $('#product-image-preview').attr('src', '');
    }

    function fillProductForm(product) {
        $('#product-id').val(product.id);
        $('#product-name').val(product.name);
        $('#product-category').val(product.category || '');
        $('#product-price').val(product.price);
        $('#product-stock').val(product.stock);
        $('#product-description').val(product.description || '');
        $('#product-image-url').val(product.imageUrl || '');
        $('#product-image-preview').attr('src', resolveImageUrl(product.imageUrl));
    }

    function loadUsers() {
        const keyword = $('#user-search').val();
        const includeInactive = $('#user-include-inactive').is(':checked');
        apiRequest({
            url: '/api/users',
            method: 'GET',
            data: { keyword: keyword, includeInactive: includeInactive }
        }).done(function (items) {
            const rows = items.map(function (u) {
                const img = resolveImageUrl(u.avatarUrl);
                const isActive = u.active !== false;
                return '<tr>' +
                    '<td>' + u.id + '</td>' +
                    '<td>' + (img ? '<img class="thumb" src="' + img + '" alt="">' : '-') + '</td>' +
                    '<td>' + escapeHtml(u.username) + '</td>' +
                    '<td>' + escapeHtml(u.email) + '</td>' +
                    '<td>' + u.role + '</td>' +
                    '<td><span class="badge ' + (isActive ? 'badge-active' : 'badge-inactive') + '">' +
                    (isActive ? '有効' : '無効') + '</span></td>' +
                    '<td>' +
                    '<button class="btn-link edit-user" data-id="' + u.id + '">編集</button> ' +
                    (isActive ? '<button class="btn-link deactivate-user" data-id="' + u.id + '">無効化</button>' : '') +
                    '</td></tr>';
            }).join('');
            $('#user-table-body').html(rows || '<tr><td colspan="7">データがありません</td></tr>');
        });
    }

    function resetUserForm() {
        $('#user-form')[0].reset();
        $('#user-id').val('');
        $('#user-role').val('VIEWER');
        $('#user-avatar-preview').attr('src', '');
    }

    function fillUserForm(user) {
        $('#user-id').val(user.id);
        $('#user-username').val(user.username);
        $('#user-email').val(user.email);
        $('#user-role').val(user.role);
        $('#user-password').val('');
        $('#user-avatar-url').val(user.avatarUrl || '');
        $('#user-avatar-preview').attr('src', resolveImageUrl(user.avatarUrl));
    }

    function loadOrders() {
        apiRequest({ url: '/api/orders/recent', method: 'GET', data: { limit: 50 } }).done(function (items) {
            const rows = items.map(function (o) {
                const statusLabel = ORDER_STATUS_LABELS[o.status] || o.status;
                return '<tr>' +
                    '<td>' + escapeHtml(o.orderNumber) + '</td>' +
                    '<td>' + escapeHtml(o.username || ('#' + o.userId)) + '</td>' +
                    '<td>' + formatMoney(o.totalAmount) + '</td>' +
                    '<td>' + statusLabel + '</td>' +
                    '<td>' + formatDate(o.createdAt) + '</td>' +
                    '</tr>';
            }).join('');
            $('#order-table-body').html(rows || '<tr><td colspan="5">注文がありません</td></tr>');
        });
    }

    function uploadImage(fileInput, type, urlInput, previewImg) {
        const file = fileInput.files[0];
        if (!file) return $.Deferred().reject().promise();

        const formData = new FormData();
        formData.append('file', file);
        formData.append('type', type);

        return $.ajax({
            url: '/api/files/upload?type=' + type,
            method: 'POST',
            data: formData,
            processData: false,
            contentType: false
        }).done(function (res) {
            urlInput.val(res.url);
            previewImg.attr('src', res.url);
            showToast('画像をアップロードしました', 'success');
        }).fail(function (xhr) {
            const msg = xhr.responseJSON && xhr.responseJSON.message
                ? xhr.responseJSON.message
                : '画像のアップロードに失敗しました';
            showToast(msg, 'error');
        });
    }

    function escapeHtml(text) {
        return $('<div/>').text(text || '').html();
    }

    $(function () {
        $('.nav-item').on('click', function () {
            navigate($(this).data('page'));
        });

        $('#product-search-btn').on('click', loadProducts);
        $('#user-search-btn').on('click', loadUsers);
        $('#product-new-btn').on('click', function () {
            resetProductForm();
            navigate('product-form');
        });
        $('#user-new-btn').on('click', function () {
            resetUserForm();
            navigate('user-form');
        });
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
            const id = $('#product-id').val();
            const payload = {
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
            const id = $('#user-id').val();
            const payload = {
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
            const id = $(this).data('id');
            apiRequest({ url: '/api/products/' + id, method: 'GET' }).done(function (product) {
                fillProductForm(product);
                navigate('product-form');
            });
        });

        $(document).on('click', '.deactivate-product', function () {
            const id = $(this).data('id');
            if (!confirm('この商品を無効化しますか？')) return;
            apiRequest({ url: '/api/products/' + id + '/deactivate', method: 'POST' }).done(function () {
                showToast('商品を無効化しました', 'success');
                loadProducts();
            });
        });

        $(document).on('click', '.edit-user', function () {
            const id = $(this).data('id');
            apiRequest({ url: '/api/users/' + id, method: 'GET' }).done(function (user) {
                fillUserForm(user);
                navigate('user-form');
            });
        });

        $(document).on('click', '.deactivate-user', function () {
            const id = $(this).data('id');
            if (!confirm('このユーザーを無効化しますか？')) return;
            apiRequest({ url: '/api/users/' + id + '/deactivate', method: 'POST' }).done(function () {
                showToast('ユーザーを無効化しました', 'success');
                loadUsers();
            });
        });

        navigate('dashboard');
    });
})(jQuery);
