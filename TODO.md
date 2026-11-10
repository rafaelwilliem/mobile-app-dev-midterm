# 📋 To-Do List Proyek: Daily Planner App (Android Java + XML)

> **Ringkasan Proyek:**
> - **Platform:** Android Studio (Bahasa Java & XML Layout)
> - **Ukuran Tim:** 3–4 Mahasiswa
> - **Durasi:** 2–3 Minggu
> - **Penyimpanan:** In-memory list / Local Storage sederhana (SharedPreferences)

---

## 📌 Checklist Berdasarkan Peran Anggota Tim

### 👤 Mahasiswa 1: UI Layout & UI Controls
- [x] Buat sketsa / wireframe sederhana antarmuka aplikasi.
- [x] Setup tema aplikasi (`themes.xml`), palet warna (`colors.xml`), dan string resources (`strings.xml`).
- [x] Buat layout XML untuk `AddTaskActivity` (`activity_add_task.xml`):
  - [x] `EditText` untuk judul/nama tugas.
  - [x] `EditText` / Multiline untuk deskripsi tugas.
  - [x] `TimePicker` atau dialog waktu untuk memilih deadline/waktu tugas.
  - [x] `Button` simpan/tambah tugas & tombol batal.
- [x] Buat layout item untuk RecyclerView (`item_task.xml`):
  - [x] `TextView` judul tugas.
  - [x] `TextView` waktu/jam tugas.
  - [x] Checkbox atau indikator status selesai.
- [x] Buat layout XML untuk masing-masing Fragment:
  - [x] `fragment_dashboard.xml` (ringkasan tugas hari ini, widget focus timer).
  - [x] `fragment_task_list.xml` (`RecyclerView` + `FloatingActionButton` untuk tambah tugas).
  - [x] `fragment_history.xml` (daftar riwayat tugas yang sudah selesai).
- [x] Implementasi validasi input pada form:
  - [x] Cek field kosong (judul tidak boleh kosong).
  - [x] Tampilkan pesan error (`setError()` pada `EditText` atau `Toast`) agar aplikasi tidak crash.

---

### 👤 Mahasiswa 2: Activity, Intent & Aliran Data
- [x] Setup `MainActivity` dan daftarkan di `AndroidManifest.xml`.
- [x] Setup `AddTaskActivity` dan daftarkan di `AndroidManifest.xml`.
- [x] Implementasi navigasi Explicit Intent:
  - [x] Buka `AddTaskActivity` dari `MainActivity` (atau dari tombol FAB di TaskListFragment) menggunakan Explicit Intent.
- [x] Implementasi pengiriman data antar Activity:
  - [x] Kirim data tugas baru dari `AddTaskActivity` kembali ke `MainActivity` menggunakan `Intent.putExtra()` / `ActivityResultLauncher` (atau `startActivityForResult`).
  - [x] Ekstrak data dari Intent extras di `MainActivity` dan perbarui daftar tugas.
- [x] Buat model data `Task.java`:
  - [x] Atribut: `id`, `title`, `description`, `time`, `isCompleted`.
  - [x] Implementasikan `Serializable` atau `Parcelable` untuk memudahkan pengiriman objek via Intent.
- [x] Kelola state perpindahan layar agar tidak terjadi *memory leak* atau *NullPointerException*.

---

### 👤 Mahasiswa 3: Android Service (FocusTimerService) & Notifikasi
- [x] Buat class `FocusTimerService` yang meng-extend `Service`.
- [x] Daftarkan service di `AndroidManifest.xml` (dan tambahkan permission `POST_NOTIFICATIONS` serta `FOREGROUND_SERVICE` jika menggunakan Foreground Service).
- [x] Implementasi logika hitung mundur (Countdown Timer):
  - [x] Menggunakan `CountDownTimer` atau `Handler`/`Runnable` di dalam Service.
  - [x] Timer tetap berjalan di latar belakang meskipun Activity diminimalkan atau berpindah fragment.
- [x] Komunikasi Service ke UI:
  - [x] Kirim broadcast/update sisa waktu ke `DashboardFragment` menggunakan `LocalBroadcastManager`, `BroadcastReceiver`, atau callback/bound service.
- [x] Kontrol Timer dari UI:
  - [x] Start / Pause / Reset timer melalui tombol di `DashboardFragment`.
- [x] Notifikasi (Opsional tapi direkomendasikan):
  - [x] Tampilkan notifikasi status timer berjalan / selesai.

---

### 👤 Mahasiswa 4: Fragments, Bottom Navigation & Integrasi
*(Jika tim 3 orang, bagian ini digabung dengan Mahasiswa 1 & 2)*
- [x] Buat menu XML untuk Bottom Navigation (`res/menu/bottom_nav_menu.xml`) dengan 3 item:
  - Dashboard
  - Task List
  - History
- [x] Tambahkan `BottomNavigationView` dan `FragmentContainerView` (atau FrameLayout) pada `activity_main.xml`.
- [x] Buat class Fragment:
  - [x] `DashboardFragment.java`
  - [x] `TaskListFragment.java`
  - [x] `HistoryFragment.java`
- [x] Implementasi navigasi fragment di `MainActivity`:
  - [x] Transaksi fragment via `FragmentManager` & `FragmentTransaction` saat item menu dipilih.
- [x] Implementasi `TaskAdapter` (RecyclerView Adapter):
  - [x] `ViewHolder` pattern untuk rendering daftar tugas di `TaskListFragment`.
  - [x] Event click listener (misal: centang selesai untuk memindahkan tugas ke `HistoryFragment`).
- [x] Integrasi kontrol Focus Timer di `DashboardFragment` dengan `FocusTimerService`.
- [x] Pengujian integrasi seluruh alur dari input tugas hingga timer berjalan.

---

## 🗓️ Checklist Berdasarkan Milestones (Lini Masa)

### 🚀 Minggu 1: Perencanaan & Dasar UI
- [x] **Setup Proyek:**
  - [x] Inisialisasi project Android Studio baru (Java, Empty Views Activity).
  - [x] Setup repository Git (GitHub/GitLab) untuk kolaborasi tim.
  - [x] Konfigurasi `build.gradle` (dependensi Material Components, RecyclerView).
- [x] **Desain & UI Controls:**
  - [x] Buat layout `activity_main.xml` (BottomNav + container).
  - [x] Buat layout `activity_add_task.xml` dengan validasi visual.
  - [x] Buat layout 3 Fragment (`Dashboard`, `TaskList`, `History`).
  - [x] Buat layout item `item_task.xml`.

---

### ⚙️ Minggu 2: Fungsionalitas Inti
- [x] **Navigasi & Intent:**
  - [x] Navigasi Bottom Navigation antar 3 Fragment berfungsi mulus.
  - [x] Panggil `AddTaskActivity` dari `TaskListFragment` via Explicit Intent.
  - [x] Form input dengan `TimePicker` dan validasi anti-crash berfungsi normal.
  - [x] Kembalikan data tugas baru dan tampilkan di `RecyclerView`.
- [x] **Service & Background Process:**
  - [x] `FocusTimerService` berhasil dijalankan dan dihentikan.
  - [x] Hitung mundur timer tetap aktif saat berpindah ke tab lain (misal dari Dashboard ke History).
- [x] **Manajemen Data Sederhana:**
  - [x] Penyimpanan data in-memory (misal: Singleton / static repository) atau `SharedPreferences` agar data tersimpan selama sesi aplikasi.

---

### 🧪 Minggu 3: Integrasi, Testing & Penyusunan Laporan
- [ ] **Integrasi & Bug Fixing:**
  - [ ] Tandai tugas selesai -> pindah dari Task List ke History.
  - [ ] Testing rotasi layar (pastikan tidak force close).
  - [ ] Testing validasi form (input kosong, format waktu salah).
  - [ ] Pastikan timer berhenti dengan benar saat di-reset atau waktu habis.
- [ ] **Build & Output:**
  - [ ] Generate APK Release / Debug (`Build > Build Bundle(s) / APK(s) > Build APK(s)`).
  - [ ] Uji install file APK pada smartphone Android fisik.

---

## 📦 Checklist Pengumpulan (Submission Checklist)

- [ ] **1. Source Code Proyek:**
  - [ ] Kode bersih, rapi, dan bebas error kompilasi.
  - [ ] Push seluruh perubahan ke repository Git tim.
- [ ] **2. File APK:**
  - [ ] File `.apk` sudah di-generate dan siap diinstall untuk pengujian dosen.
- [ ] **3. Laporan Singkat:**
  - [ ] Penjelasan arsitektur aplikasi (Activity, Fragment, Service, Data Flow).
  - [ ] Dokumentasi 4 materi pokok: UI Layout/Controls, Activity & Intent, Fragment, Service.
  - [ ] Pembagian tugas masing-masing anggota kelompok.
- [ ] **4. Tangkapan Layar (Screenshots):**
  - [ ] Screenshot halaman Dashboard (dengan timer).
  - [ ] Screenshot form Tambah Tugas (dengan validasi/TimePicker).
  - [ ] Screenshot Task List (RecyclerView).
  - [ ] Screenshot History Fragment.
- [ ] **5. Persiapan Demo (Live Demo 5–10 Menit):**
  - [ ] Skenario demo: Alur tambah tugas -> Menjalankan Focus Timer -> Pindah-pindah tab navigasi -> Menyelesaikan tugas.
- [ ] **6. Penjelasan Individu:**
  - [ ] Setiap anggota memahami dan siap menjelaskan baris kode yang menjadi tanggung jawabnya.
