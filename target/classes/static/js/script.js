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
});
