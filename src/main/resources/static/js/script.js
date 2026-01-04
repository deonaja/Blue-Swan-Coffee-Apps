// Creative UI Mobile Menu Logic
document.addEventListener('DOMContentLoaded', () => {
    const hamburgerBtn = document.getElementById('hamburger-menu');
    
    if (hamburgerBtn) {
        // Create Mobile Menu Overlay dynamically
        const mobileMenuOverlay = document.createElement('div');
        mobileMenuOverlay.id = 'mobile-menu-overlay';
        mobileMenuOverlay.className = 'fixed inset-0 bg-[#d9c6b0]/95 backdrop-blur-xl z-[100] transform translate-x-full transition-transform duration-500 ease-in-out flex flex-col items-center justify-center';
        
        // Mobile Menu Content
        mobileMenuOverlay.innerHTML = `
            <button id="close-menu" class="absolute top-8 right-8 text-[#1a1a1a] p-2 hover:rotate-90 transition-transform duration-300">
                <i data-feather="x" class="w-10 h-10"></i>
            </button>
            <div class="flex flex-col gap-8 text-center">
                <a href="/" class="mobile-link text-4xl font-bold text-[#1a1a1a] hover:text-primary transition-colors">Home</a>
                <a href="/#about" class="mobile-link text-4xl font-bold text-[#1a1a1a] hover:text-primary transition-colors">About</a>
                <a href="/menu" class="mobile-link text-4xl font-bold text-[#1a1a1a] hover:text-primary transition-colors">Menu</a>
                <a href="/#contact" class="mobile-link text-4xl font-bold text-[#1a1a1a] hover:text-primary transition-colors">Contact</a>
                <div class="w-20 h-1 bg-primary rounded-full mx-auto my-4"></div>
                <div class="flex gap-6 justify-center">
                    <a href="/cart" class="text-[#1a1a1a] hover:text-primary"><i data-feather="shopping-cart" class="w-8 h-8"></i></a>
                    <a href="/login" class="text-[#1a1a1a] hover:text-primary"><i data-feather="user" class="w-8 h-8"></i></a>
                </div>
            </div>
        `;
        
        document.body.appendChild(mobileMenuOverlay);
        feather.replace();

        const closeBtn = document.getElementById('close-menu');
        const mobileLinks = mobileMenuOverlay.querySelectorAll('.mobile-link');

        function toggleMenu() {
            const isClosed = mobileMenuOverlay.classList.contains('translate-x-full');
            if (isClosed) {
                mobileMenuOverlay.classList.remove('translate-x-full');
                document.body.style.overflow = 'hidden'; // Prevent scrolling
            } else {
                mobileMenuOverlay.classList.add('translate-x-full');
                document.body.style.overflow = '';
            }
        }

        hamburgerBtn.addEventListener('click', (e) => {
            e.preventDefault();
            toggleMenu();
        });

        closeBtn.addEventListener('click', toggleMenu);

        // Close menu when a link is clicked
        mobileLinks.forEach(link => {
            link.addEventListener('click', toggleMenu);
        });
    }

    // AJAX Add to Cart Logic
    const cartForms = document.querySelectorAll('form[action="/cart/add"]');
    cartForms.forEach(form => {
        form.addEventListener('submit', async (e) => {
            e.preventDefault();
            
            const submitBtn = form.querySelector('button[type="submit"]');
            const originalContent = submitBtn.innerHTML;
            
            // Loading State - detect button type
            const isRoundButton = submitBtn.classList.contains('rounded-full') && submitBtn.classList.contains('w-12');
            submitBtn.disabled = true;
            if (isRoundButton) {
                // Menu page: just spinner icon
                submitBtn.innerHTML = '<i data-feather="loader" class="w-6 h-6 animate-spin"></i>';
            } else {
                // Detail page: spinner with text
                submitBtn.innerHTML = '<i data-feather="loader" class="w-4 h-4 animate-spin"></i> Adding...';
            }
            feather.replace();

            // Get CSRF Token
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');
            
            try {
                const formData = new FormData(form);
                
                // Add minimum delay so loading animation is visible
                const [response] = await Promise.all([
                    fetch('/cart/add', {
                        method: 'POST',
                        headers: {
                            'X-Requested-With': 'XMLHttpRequest',
                            [csrfHeader]: csrfToken
                        },
                        body: formData
                    }),
                    new Promise(resolve => setTimeout(resolve, 500)) // minimum 500ms delay
                ]);

                if (response.ok) {
                    const data = await response.json();
                    if (data.success) {
                        // Success Feedback - check if it's a round button (menu page) or text button
                        const isRoundButton = submitBtn.classList.contains('rounded-full') && submitBtn.classList.contains('w-12');
                        if (isRoundButton) {
                            // Menu page: just show checkmark icon
                            submitBtn.innerHTML = '<i data-feather="check" class="w-6 h-6"></i>';
                        } else {
                            // Detail page: show "Added" text with checkmark
                            submitBtn.innerHTML = '<i data-feather="check" class="w-4 h-4"></i> Added';
                        }
                        feather.replace();
                        submitBtn.classList.add('bg-primary', 'text-white');
                        submitBtn.classList.remove('bg-[#1a1a1a]');
                        submitBtn.disabled = false; // Enable again to allow adding more

                        // Update Cart Badge
                        const cartIcons = document.querySelectorAll('a[href*="/cart"]');
                        cartIcons.forEach(iconLink => {
                            // Check if badge exists
                            let badge = iconLink.querySelector('span');
                            if (!badge) {
                                // Create badge if not exists
                                badge = document.createElement('span');
                                badge.className = 'absolute top-0 right-0 w-2.5 h-2.5 bg-red-500 rounded-full border border-white animate-bounce';
                                iconLink.appendChild(badge);
                                iconLink.classList.add('relative');
                            } else {
                                // Animate existing badge
                                badge.classList.remove('animate-bounce');
                                void badge.offsetWidth; // trigger reflow
                                badge.classList.add('animate-bounce');
                            }
                        });
                        
                        // Reset button after 2 seconds
                        setTimeout(() => {
                            submitBtn.innerHTML = originalContent;
                            submitBtn.disabled = false;
                            submitBtn.classList.remove('bg-primary');
                            submitBtn.classList.add('bg-[#1a1a1a]');
                            feather.replace();
                        }, 2000);
                    }
                } else if (response.status === 401) {
                     window.location.href = '/login';
                }
            } catch (error) {
                console.error('Error adding to cart:', error);
                submitBtn.innerHTML = originalContent;
                submitBtn.disabled = false;
                feather.replace();
            }
        });
    });

    // AJAX Toggle Favorite Logic
    const favoriteBtns = document.querySelectorAll('.favorite-btn');
    favoriteBtns.forEach(btn => {
        btn.addEventListener('click', async (e) => {
            e.preventDefault();
            e.stopPropagation();
            
            const itemId = btn.getAttribute('data-item-id');
            if (!itemId) return;
            
            // Disable button during request
            btn.disabled = true;
            
            // Get CSRF Token
            const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
            const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

            try {
                const response = await fetch(`/menu/favorite/${itemId}`, {
                    method: 'POST',
                    headers: {
                        'X-Requested-With': 'XMLHttpRequest',
                         [csrfHeader]: csrfToken
                    }
                });

                if (response.ok) {
                    const data = await response.json();
                    if (data.success) {
                        // Toggle visual state
                        if (data.favorited) {
                            btn.classList.add('favorited', 'bg-red-500', 'text-white');
                            btn.classList.remove('bg-white/80', 'text-red-500');
                            // Update icon to filled
                            const icon = btn.querySelector('svg');
                            if (icon) icon.classList.add('fill-current');
                        } else {
                            btn.classList.remove('favorited', 'bg-red-500', 'text-white');
                            btn.classList.add('bg-white/80', 'text-red-500');
                            // Update icon to outline
                            const icon = btn.querySelector('svg');
                            if (icon) icon.classList.remove('fill-current');
                        }
                    }
                } else if (response.status === 401) {
                    window.location.href = '/login';
                }
            } catch (error) {
                console.error('Error toggling favorite:', error);
            } finally {
                btn.disabled = false;
            }
        });
    });

    // AJAX Cart Quantity Update Logic
    const qtyInputs = document.querySelectorAll('.qty-input');
    let updateTimeout = null;
    
    qtyInputs.forEach(input => {
        input.addEventListener('change', async (e) => {
            const productId = input.getAttribute('data-product-id');
            const quantity = parseInt(input.value);
            
            if (!productId || isNaN(quantity) || quantity < 1) return;
            
            // Clamp quantity to valid range
            if (quantity > 10) input.value = 10;
            if (quantity < 1) input.value = 1;
            
            // Visual feedback - show loading state
            input.classList.add('opacity-50');
            
            // Debounce to avoid too many requests
            if (updateTimeout) clearTimeout(updateTimeout);
            
            updateTimeout = setTimeout(async () => {
                try {
                    const formData = new FormData();
                    formData.append('productId', productId);
                    formData.append('quantity', input.value);
                    
                    // Get CSRF Token
                    const csrfToken = document.querySelector('meta[name="_csrf"]').getAttribute('content');
                    const csrfHeader = document.querySelector('meta[name="_csrf_header"]').getAttribute('content');

                    const response = await fetch('/cart/update', {
                        method: 'POST',
                        headers: {
                            'X-Requested-With': 'XMLHttpRequest',
                            [csrfHeader]: csrfToken
                        },
                        body: formData
                    });
                    
                    if (response.ok) {
                        const data = await response.json();
                        if (data.success) {
                            // Format number with 2 decimal places
                            const formatPrice = (num) => parseFloat(num).toFixed(2);
                            
                            // Update item total in the same row
                            const row = input.closest('.cart-item');
                            if (row) {
                                const itemTotalCell = row.querySelector('.item-total');
                                if (itemTotalCell) {
                                    itemTotalCell.textContent = 'IDR ' + formatPrice(data.itemTotal);
                                }
                            }
                            
                            // Update cart subtotal and total
                            const subtotalEl = document.getElementById('cart-subtotal');
                            const totalEl = document.getElementById('cart-total');
                            if (subtotalEl) subtotalEl.textContent = formatPrice(data.cartTotal);
                            if (totalEl) totalEl.textContent = formatPrice(data.cartTotal);
                        }
                    } else if (response.status === 401) {
                        window.location.href = '/login';
                    }
                } catch (error) {
                    console.error('Error updating cart:', error);
                } finally {
                    input.classList.remove('opacity-50');
                }
            }, 300); // 300ms debounce
        });
    });
});
