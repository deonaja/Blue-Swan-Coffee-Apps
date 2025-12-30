# Blue Swan Coffee 🦢☕

Selamat datang di repositori proyek **Blue Swan Coffee**. Ini adalah aplikasi web berbasis Spring Boot yang dirancang untuk pengalaman kedai kopi premium, menampilkan desain UI "Glassmorphism" yang unik.

## 🌟 Fitur

*   **Desain UI Kreatif**: Antarmuka modern yang memukau dengan kartu kaca mengambang (floating glass cards), elemen latar belakang animasi, dan pengalaman pengguna yang mulus.
*   **Otentikasi Pengguna**:
    *   **Login**: Masuk aman untuk pelanggan yang sudah terdaftar.
    *   **Registrasi**: Pengguna baru dapat mendaftar dengan mudah sebagai Pelanggan.
*   **Menu Produk**:
    *   Jelajahi berbagai pilihan kopi, non-kopi, camilan, dan biji kopi.
    *   Tata letak grid gaya masonry untuk tampilan produk.
*   **Keranjang Belanja**:
    *   Tambahkan item ke keranjang.
    *   Lihat ringkasan keranjang (Subtotal, Pajak, Total).
    *   Hapus item.
*   **Desain Responsif**: Dioptimalkan sepenuhnya untuk Desktop, Tablet, dan Ponsel (termasuk menu overlay seluler kustom).

## 🛠️ Tech Stack

*   **Backend**: Java (Spring Boot)
*   **Frontend**: HTML (Thymeleaf), CSS (Tailwind CSS via CDN), JavaScript
*   **Database**: MySQL
*   **Build Tool**: Maven

## 🚀 Memulai (Getting Started)

### Prasyarat

*   Java 17 atau lebih tinggi
*   Maven
*   MySQL Server

### Instalasi

1.  **Clone repositori**
    ```bash
    git clone https://github.com/if-tel-u/proyek-akhir-kelompok-3-ocd.git
    cd proyek-akhir-kelompok-3-ocd
    ```

3.  **Konfigurasi Database**
    *   Pastikan MySQL sedang berjalan.
    *   Buat database bernama `db_blueswan` (opsional, karena aplikasi akan membuatnya otomatis).
    *   Copy file konfigurasi template:
        **Windows**:
        ```bash
        copy src\main\resources\application-local.properties.example src\main\resources\application-local.properties
        ```
        **Mac/Linux**:
        ```bash
        cp src/main/resources/application-local.properties.example src/main/resources/application-local.properties
        ```
    *   Edit file `src/main/resources/application-local.properties` dan sesuaikan username/password database Anda.
    *   **Catatan Penting**:
        *   Jangan edit `application.properties` untuk config database, file itu khusus untuk profile `local`.
        *   Defaultnya, aplikasi akan melakukan **Auto-Seeding (Reset Data)** setiap kali dijalankan (`ddl-auto=create`). Modifikasi file local ini jika ingin mengubah perilaku tersebut.

3.  **Jalankan Aplikasi**
    ```bash
    .\mvnw spring-boot:run
    ```

4.  **Akses Aplikasi**
    Buka browser Anda dan kunjungi: `http://localhost:8080`

## 🎨 Sorotan UI

Aplikasi ini menjauh dari tampilan template standar, menawarkan:
*   **Glassmorphism**: Elemen tembus pandang dengan efek blur (`backdrop-blur`).
*   **Tata Letak Asimetris**: Desain Hero dan Seksi yang dinamis.
*   **Palet Warna Ketat**:
    *   Utama: `#4a6fa5` (Blue Swan)
    *   Latar Belakang: `#d9c6b0` (Warm Beige)

## 🤝 Alur Kerja Proyek (Git)

Untuk anggota tim yang berkontribusi pada proyek ini (Cara Kerja GitHub):

1.  **Ambil Repo (Clone)** (Hanya pertama kali)
    ```bash
    git clone https://github.com/if-tel-u/proyek-akhir-kelompok-3-ocd.git
    ```

2.  **Ambil Update Terbaru** (Sebelum mulai kerja)
    ```bash
    git pull origin main
    ```

3.  **Lakukan Perubahan (Coding)**
    *   Disarankan membuat branch baru untuk setiap fitur:
        ```bash
        git checkout -b nama-fitur
        ```
    *   Setelah mengedit file, simpan perubahan (Stage & Commit):
        ```bash
        git add .
        git commit -m "Menambahkan fitur X baru"
        ```

4.  **Upload Perubahan (Push)**
    ```bash
    git push origin main
    # Atau jika menggunakan branch: git push origin nama-fitur
    ```

## 👥 Kontributor

*   **Kelompok 3 OCD**
*   **MIlhamRidhoP**
*   **Sofwanrsd | sofwandev**
*   **Deonaja**

---
&copy; 2025 Blue Swan Coffee. Hak Cipta Dilindungi Undang-Undang.
