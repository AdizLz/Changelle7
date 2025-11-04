// Manejo de WebSocket y actualizaciones en tiempo real
(function setupWebSocket() {
    // Evitar abrir múltiples conexiones: reutilizar si ya existe
    if (window.__priceWs && (window.__priceWs.readyState === WebSocket.OPEN || window.__priceWs.readyState === WebSocket.CONNECTING)) {
        console.log('🔁 Reutilizando WebSocket existente');
        return;
    }

    const statusEl = document.getElementById('ws-status');

    function setStatus(connected) {
        if (!statusEl) return;
        if (connected) {
            statusEl.className = 'ws-status ws-connected';
            statusEl.innerHTML = '<i class="bi bi-circle-fill"></i> Conectado';
        } else {
            statusEl.className = 'ws-status ws-disconnected';
            statusEl.innerHTML = '<i class="bi bi-circle-fill"></i> Desconectado';
        }
    }

    let reconnectTimer = null;

    function connect() {
        try {
            const protocol = window.location.protocol === 'https:' ? 'wss:' : 'ws:';
            const wsUrl = `${protocol}//${window.location.host}/ws/prices`;
            console.log('🌐 Abriendo WebSocket a', wsUrl);

            const ws = new WebSocket(wsUrl);
            window.__priceWs = ws; // guardar singleton global

            ws.onopen = () => {
                console.log('🔌 WebSocket conectado');
                clearTimeout(reconnectTimer);
                setStatus(true);
            };

            ws.onmessage = (event) => {
                try {
                    const data = JSON.parse(event.data);
                    // Actualización de lista
                    if (data.type === 'price_update' && data.itemId && data.newPrice) {
                        const listPrice = document.querySelector(`[data-item-id="${data.itemId}"] .item-price`);
                        if (listPrice) {
                            listPrice.textContent = data.newPrice;
                            listPrice.classList.add('price-updated');
                            setTimeout(() => listPrice.classList.remove('price-updated'), 600);
                        }
                        // Actualización de detalle
                        const detailPrice = document.getElementById('current-price') || document.getElementById('price-display');
                        if (detailPrice && detailPrice.getAttribute('data-item-id') === String(data.itemId)) {
                            detailPrice.innerHTML = `<i class="bi bi-tag-fill"></i> ${data.newPrice}`;
                            detailPrice.classList.add('price-updated');
                            setTimeout(() => detailPrice.classList.remove('price-updated'), 600);
                        }
                    }
                } catch (e) {
                    console.error('❌ Error procesando mensaje WS:', e);
                }
            };

            ws.onerror = (err) => {
                console.error('⚠️ WebSocket error:', err);
            };

            ws.onclose = (evt) => {
                console.log('🔌 WebSocket cerrado', evt && evt.reason ? '(' + evt.reason + ')' : '');
                setStatus(false);
                clearTimeout(reconnectTimer);
                reconnectTimer = setTimeout(connect, 5000);
            };
        } catch (e) {
            console.error('❌ No se pudo crear WebSocket:', e);
            setStatus(false);
            clearTimeout(reconnectTimer);
            reconnectTimer = setTimeout(connect, 5000);
        }
    }

    connect();
})();

// Manejo del formulario de ofertas
document.addEventListener('DOMContentLoaded', function() {
    const offerBtn = document.querySelector('.offer-btn');
    const offerForm = document.getElementById('offer-form');

    if (offerBtn && offerForm) {
        // Mostrar/ocultar formulario
        offerBtn.addEventListener('click', function() {
            if (offerForm.style.display === 'none' || offerForm.style.display === '') {
                offerForm.style.display = 'block';
                offerBtn.textContent = '✕ Cerrar formulario';
            } else {
                offerForm.style.display = 'none';
                offerBtn.innerHTML = '<i class="bi bi-hand-thumbs-up"></i> Hacer una Oferta';
            }
        });

        // Enviar oferta con AJAX
        offerForm.addEventListener('submit', function(e) {
            e.preventDefault();

            const name = document.getElementById('name-input').value.trim();
            const email = document.getElementById('email-input').value.trim();
            const amountRaw = document.getElementById('amount-input').value;
            const amount = parseFloat(amountRaw);
            const itemId = document.getElementById('item-id').value;

            if (!name || !email || isNaN(amount) || amount <= 0) {
                alert('Por favor completa todos los campos correctamente y asegúrate de que el monto sea mayor que 0.');
                return;
            }

            const payload = {
                name: name,
                email: email,
                id: itemId,
                amount: amount
            };

            const submitBtn = offerForm.querySelector('button[type="submit"]');
            if (submitBtn) {
                submitBtn.disabled = true;
                submitBtn.textContent = 'Enviando...';
            }

            fetch('/api/offers', {
                method: 'POST',
                headers: { 'Content-Type': 'application/json' },
                body: JSON.stringify(payload)
            })
            .then(response => response.json())
            .then(data => {
                if (data.success) {
                    // Mostrar mensaje de éxito
                    const successAlert = document.createElement('div');
                    successAlert.className = 'alert alert-success alert-dismissible fade show';
                    successAlert.innerHTML = `
                        <strong>¡Éxito!</strong> Tu oferta de $${amount.toFixed(2)} USD ha sido registrada.
                        <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                    `;
                    offerForm.insertAdjacentElement('beforebegin', successAlert);

                    // Limpiar y ocultar el formulario
                    offerForm.reset();
                    offerForm.style.display = 'none';
                    offerBtn.innerHTML = '<i class="bi bi-hand-thumbs-up"></i> Hacer otra Oferta';

                    // Actualizar el precio mostrado inmediatamente
                    const priceDisplay = document.getElementById('current-price') || document.getElementById('price-display');
                    if (priceDisplay) {
                        priceDisplay.innerHTML = `<i class="bi bi-tag-fill"></i> ${data.newPrice}`;
                        priceDisplay.classList.add('price-updated');
                        setTimeout(() => priceDisplay.classList.remove('price-updated'), 1000);
                    }

                    // Actualizar el contador de ofertas si existe
                    const offerCountElement = document.querySelector('.alert-info strong');
                    if (offerCountElement) {
                        const currentCount = parseInt(offerCountElement.textContent) || 0;
                        offerCountElement.textContent = currentCount + 1;
                    }

                    // Ocultar la alerta después de 5 segundos
                    setTimeout(() => {
                        successAlert.classList.remove('show');
                        setTimeout(() => successAlert.remove(), 150);
                    }, 5000);
                } else {
                    throw new Error(data.message || 'Error al procesar la oferta');
                }
            })
            .catch(error => {
                const errorAlert = document.createElement('div');
                errorAlert.className = 'alert alert-danger alert-dismissible fade show';
                errorAlert.innerHTML = `
                    <strong>Error:</strong> ${error.message}
                    <button type="button" class="btn-close" data-bs-dismiss="alert"></button>
                `;
                offerForm.insertAdjacentElement('beforebegin', errorAlert);

                setTimeout(() => {
                    errorAlert.classList.remove('show');
                    setTimeout(() => errorAlert.remove(), 150);
                }, 5000);
            })
            .finally(() => {
                if (submitBtn) {
                    submitBtn.disabled = false;
                    submitBtn.innerHTML = '<i class="bi bi-send-fill"></i> Enviar Oferta';
                }
            });
        });
    }

    // -------------------------
    // FILTROS Y RENDER DE ITEMS
    // -------------------------
    const searchInput = document.getElementById('search-input');
    const minPriceInput = document.getElementById('min-price');
    const maxPriceInput = document.getElementById('max-price');
    const filterBtn = document.getElementById('filter-btn');
    const clearBtn = document.getElementById('clear-btn');
    const itemsGrid = document.getElementById('items-grid');

    function buildQueryParams(q, min, max) {
        const params = new URLSearchParams();
        if (q && q.trim()) params.set('q', q.trim());
        if (min !== null && min !== undefined && min !== '') params.set('minPrice', min);
        if (max !== null && max !== undefined && max !== '') params.set('maxPrice', max);
        return params.toString() ? ('?' + params.toString()) : '';
    }

    async function fetchItems(q, min, max) {
        try {
            const qs = buildQueryParams(q, min, max);
            console.log('🔍 Fetching items with params:', qs);
            const res = await fetch('/api/items' + qs);
            if (!res.ok) throw new Error('Error fetching items: ' + res.status);
            const data = await res.json();
            console.log('📦 Received items:', data.length);
            renderItems(data);
        } catch (err) {
            console.error('❌ Error fetching items:', err);
            if (itemsGrid) {
                itemsGrid.innerHTML = '<div class="col-12"><div class="alert alert-danger text-center" role="alert"><i class="bi bi-exclamation-triangle"></i> Error al cargar los artículos. Intenta nuevamente.</div></div>';
            }
        }
    }

    function renderItems(items) {
        if (!itemsGrid) return;
        itemsGrid.innerHTML = '';

        if (!items || items.length === 0) {
            itemsGrid.innerHTML = '<div class="col-12"><div class="alert alert-info text-center" role="alert"><i class="bi bi-info-circle"></i> No se encontraron artículos con los filtros aplicados.</div></div>';
            return;
        }

        console.log('✅ Rendering', items.length, 'items');

        for (const it of items) {
            const col = document.createElement('div');
            col.className = 'col-md-6 col-lg-4';
            col.setAttribute('data-item-id', it.id);
            col.setAttribute('data-item-price', it.price);

            const card = document.createElement('div');
            card.className = 'card item-card';

            const body = document.createElement('div');
            body.className = 'card-body item-card-body';

            body.innerHTML = `
                <div class="d-flex justify-content-between align-items-start mb-3">
                    <h5 class="item-name flex-grow-1">${escapeHtml(it.name)}</h5>
                    <i class="bi bi-star-fill text-warning"></i>
                </div>
                <div class="d-flex justify-content-between align-items-center">
                    <span class="item-price">${escapeHtml(it.price)}</span>
                    <a href="/items/${encodeURIComponent(it.id)}" class="btn btn-view">Ver detalles <i class="bi bi-arrow-right"></i></a>
                </div>
            `;

            card.appendChild(body);
            col.appendChild(card);
            itemsGrid.appendChild(col);
        }
    }

    function escapeHtml(str) {
        if (str == null) return '';
        return String(str).replace(/[&<>\"'`]/g, function (s) {
            return ({'&':'&amp;','<':'&lt;','>':'&gt;','"':'&quot;',"'":'&#39;','`':'&#96;'}[s]);
        });
    }

    // Event: Aplicar filtros
    filterBtn && filterBtn.addEventListener('click', function() {
        const q = searchInput ? searchInput.value.trim() : '';
        const min = minPriceInput ? minPriceInput.value.trim() : '';
        const max = maxPriceInput ? maxPriceInput.value.trim() : '';
        console.log('🔎 Aplicando filtros:', { q, min, max });
        fetchItems(q, min, max);
    });

    // Event: Limpiar filtros
    clearBtn && clearBtn.addEventListener('click', function() {
        if (searchInput) searchInput.value = '';
        if (minPriceInput) minPriceInput.value = '';
        if (maxPriceInput) maxPriceInput.value = '';
        console.log('🧹 Limpiando filtros');
        fetchItems('', '', '');
    });

    // Allow pressing Enter in any filter input to trigger filter
    [searchInput, minPriceInput, maxPriceInput].forEach(input => {
        if (input) {
            input.addEventListener('keydown', function(e) {
                if (e.key === 'Enter') {
                    e.preventDefault();
                    filterBtn && filterBtn.click();
                }
            });
        }
    });
});
