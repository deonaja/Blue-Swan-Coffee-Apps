# MyCourt UI Implementation Plan

## Goal Description
Create the Front-End structure and UI Design for "MyCourt" using Laravel Blade and Tailwind CSS. The focus is on a responsive, role-based layout and key pages for the booking flow.

## Proposed Changes

### View Structure
Reorganize `resources/views` to support roles:
- `layouts/app.blade.php`: Master layout.
- `partials/navbar.blade.php`: Dynamic navbar.
- `partials/footer.blade.php`: Footer.
- `user/`: Pages for the booking flow.
- `admin/`: Pages for admin dashboard.
- `manager/`: Pages for field management.
- `welcome.blade.php`: Modern landing page.

### Layouts
#### [NEW] `resources/views/layouts/app.blade.php`
- Master layout with Tailwind CSS CDN (or Vite directive).
- Includes Navbar and Footer.
- Slots for content.

#### [NEW] `resources/views/partials/navbar.blade.php`
- Component logic to check `Auth::user()->role` (mocked or actual if fields exist).
- Links:
    - Guest: Home, Fields, Login, Register.
    - User: Home, History, Notif, Logout.
    - Admin/Manager: Dashboard, Logout.

### Pages
#### [MODIFY] `resources/views/welcome.blade.php`
- Hero section with "Search Field" CTA.
- Highlights section.
- Footer.

## Verification Plan
### Manual Verification
- Render the Landing Page (`/`).
- Check responsiveness on mobile/desktop dimensions.
- Verify Navbar links appear correctly (I will verify this by visually checking the code primarily, as I cannot login without a database setup, though I can simulate auth states in Blade).
