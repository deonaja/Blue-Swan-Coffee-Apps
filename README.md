[![Review Assignment Due Date](https://classroom.github.com/assets/deadline-readme-button-22041afd0340ce965d47ae6ef1cefeee28c7c493a6346c4f15d667ab976d596c.svg)](https://classroom.github.com/a/UsrmZe0X)

# Blue Swan Coffee

## Deskripsi
**Blue Swan Coffee** adalah aplikasi web komprehensif yang dirancang untuk kedai kopi modern. Aplikasi ini memberikan pengalaman e-commerce yang mulus bagi pelanggan untuk menelusuri menu, mengelola keranjang belanja, dan melakukan pemesanan. Sistem ini dibangun dengan backend Spring Boot yang tangguh dan mengikuti arsitektur MVC yang bersih.

## Fitur
*   **Otentikasi Pengguna**: Login dan Logout yang aman dengan manajemen sesi di sisi server.
*   **Akses Berbasis Peran (Role-Based Access)**:
    *   **Customer**: Menelusuri menu, menambah item ke keranjang, checkout.
    *   **Admin/Barista**: (Infrastruktur siap untuk fitur khusus peran di masa depan).
*   **Menu Dinamis**: Tampilan item menu secara real-time dari database.
*   **Sistem Keranjang Belanja**: Menambah item, menyesuaikan jumlah, dan menghitung total harga secara real-time.
*   **Pemrosesan Pesanan**: Alur checkout sederhana yang menangani pembuatan dan penyimpanan pesanan.
*   **UI Responsif**: Desain estetis yang sesuai dengan identitas brand, responsif penuh untuk berbagai perangkat (Laptop, Tablet, HP).

## Teknologi yang Digunakan
*   **Java 17**
*   **Spring Boot**: Web, Data JPA, Security (Konfigurasi Custom)
*   **Database**: MySQL
*   **Template Engine**: Thymeleaf
*   **Frontend**: HTML5, CSS3 (Custom), JavaScript (Feather Icons)
*   **Build Tool**: Maven

## Prasyarat
Sebelum menjalankan proyek ini, pastikan Anda telah menginstal:
*   **Java Development Kit (JDK) 17** atau lebih baru.
*   **MySQL Server**.

## Instalasi & Pengaturan

1.  **Clone Repository**
    ```bash
    git clone https://github.com/if-tel-u/proyek-akhir-kelompok-3-ocd.git
    cd pbo-coffee-shop
    ```

2.  **Konfigurasi Database**
    *   Pastikan server MySQL Anda berjalan.
    *   Aplikasi dikonfigurasi untuk membuat database `db_blueswan` secara otomatis jika belum ada.
    *   Buka file `src/main/resources/application.properties` dan verifikasi kredensial database Anda:
        ```properties
        # Konfigurasi default menggunakan port 3307. Ubah ke 3306 jika menggunakan port standar.
        spring.datasource.url=jdbc:mysql://localhost:3307/db_blueswan?createDatabaseIfNotExist=true&serverTimezone=UTC
        spring.datasource.username=root
        spring.datasource.password=root
        ```
    *   *Catatan: Jika password root MySQL Anda berbeda, perbarui field `spring.datasource.password` sesuai dengan password Anda.*

3.  **Jalankan Aplikasi**
    Anda dapat menjalankan aplikasi menggunakan Maven wrapper yang disertakan:
    ```bash
    # Untuk Windows
    ./mvnw spring-boot:run

    # Untuk Linux/Mac
    ./mvnw spring-boot:run
    ```

4.  **Akses Aplikasi**
    Setelah aplikasi berjalan, buka browser Anda dan kunjungi:
    **[http://localhost:8080](http://localhost:8080)**

## Akun Bawaan (Default Credentials)
Aplikasi secara otomatis mengisi database dengan pengguna berikut untuk pengujian awal:

| Peran (Role) | Email | Password |
| :--- | :--- | :--- |
| **Customer** | `customer@blueswan.com` | `user123` |
| **Admin** | `admin@blueswan.com` | `admin123` |
| **Barista** | `barista@blueswan.com` | `barista123` |

## Struktur Proyek
*   `com.blueswancoffee.controller`: Menangani request web (MVC Pattern).
*   `com.blueswancoffee.model`: Entitas JPA (Tabel Database).
*   `com.blueswancoffee.repository`: Layer Akses Data (DAO).
*   `com.blueswancoffee.service`: Logika Bisnis.
*   `com.blueswancoffee.config`: Konfigurasi Keamanan dan Aplikasi.
*   `resources/templates`: Tampilan HTML (Thymeleaf).
*   `resources/static`: File statis (CSS, Gambar, JS).

---
&copy; 2024 Blue Swan Coffee Project.
