# 📋 To-Do List Proyek: Daily Planner App (Android Java + XML)

> **Ringkasan Proyek:**
> - **Platform:** Android Studio (Bahasa Java & XML Layout)
> - **Ukuran Tim:** 3–4 Mahasiswa
> - **Durasi:** 2–3 Minggu
> - **Penyimpanan:** In-memory list / Local Storage sederhana (SharedPreferences)

---

## 📌 Checklist Berdasarkan Peran Anggota Tim

### 👤 Mahasiswa 1: UI Layout & UI Controls
- [ ] Buat sketsa / wireframe sederhana antarmuka aplikasi.
- [ ] Setup tema aplikasi (`themes.xml`), palet warna (`colors.xml`), dan string resources (`strings.xml`).
- [ ] Buat layout XML untuk `AddTaskActivity` (`activity_add_task.xml`):
  - [ ] `EditText` untuk judul/nama tugas.
  - [ ] `EditText` / Multiline untuk deskripsi tugas.
  - [ ] `TimePicker` atau dialog waktu untuk memilih deadline/waktu tugas.
  - [ ] `Button` simpan/tambah tugas & tombol batal.
- [ ] Buat layout item untuk RecyclerView (`item_task.xml`):
  - [ ] `TextView` judul tugas.
  - [ ] `TextView` waktu/jam tugas.
  - [ ] Checkbox atau indikator status selesai.
- [ ] Buat layout XML untuk masing-masing Fragment:
  - [ ] `fragment_dashboard.xml` (ringkasan tugas hari ini, widget focus timer).
  - [ ] `fragment_task_list.xml` (`RecyclerView` + `FloatingActionButton` untuk tambah tugas).
  - [ ] `fragment_history.xml` (daftar riwayat tugas yang sudah selesai).
- [ ] Implementasi validasi input pada form:
  - [ ] Cek field kosong (judul tidak boleh kosong).
  - [ ] Tampilkan pesan error (`setError()` pada `EditText` atau `Toast`) agar aplikasi tidak crash.

---

### 👤 Mahasiswa 2: Activity, Intent & Aliran Data
- [ ] Setup `MainActivity` dan daftarkan di `AndroidManifest.xml`.
- [ ] Setup `AddTaskActivity` dan daftarkan di `AndroidManifest.xml`.
- [ ] Implementasi navigasi Explicit Intent:
  - [ ] Buka `AddTaskActivity` dari `MainActivity` (atau dari tombol FAB di TaskListFragment) menggunakan Explicit Intent.
- [ ] Implementasi pengiriman data antar Activity:
  - [ ] Kirim data tugas baru dari `AddTaskActivity` kembali ke `MainActivity` menggunakan `Intent.putExtra()` / `ActivityResultLauncher` (atau `startActivityForResult`).
  - [ ] Ekstrak data dari Intent extras di `MainActivity` dan perbarui daftar tugas.
- [ ] Buat model data `Task.java`:
  - [ ] Atribut: `id`, `title`, `description`, `time`, `isCompleted`.
  - [ ] Implementasikan `Serializable` atau `Parcelable` untuk memudahkan pengiriman objek via Intent.
- [ ] Kelola state perpindahan layar agar tidak terjadi *memory leak* atau *NullPointerException*.

---

### 👤 Mahasiswa 3: Android Service (FocusTimerService) & Notifikasi
- [ ] Buat class `FocusTimerService` yang meng-extend `Service`.
- [ ] Daftarkan service di `AndroidManifest.xml` (dan tambahkan permission `POST_NOTIFICATIONS` serta `FOREGROUND_SERVICE` jika menggunakan Foreground Service).
- [ ] Implementasi logika hitung mundur (Countdown Timer):
  - [ ] Menggunakan `CountDownTimer` atau `Handler`/`Runnable` di dalam Service.
  - [ ] Timer tetap berjalan di latar belakang meskipun Activity diminimalkan atau berpindah fragment.
- [ ] Komunikasi Service ke UI:
  - [ ] Kirim broadcast/update sisa waktu ke `DashboardFragment` menggunakan `LocalBroadcastManager`, `BroadcastReceiver`, atau callback/bound service.
- [ ] Kontrol Timer dari UI:
  - [ ] Start / Pause / Reset timer melalui tombol di `DashboardFragment`.
- [ ] Notifikasi (Opsional tapi direkomendasikan):
  - [ ] Tampilkan notifikasi status timer berjalan / selesai.

---

### 👤 Mahasiswa 4: Fragments, Bottom Navigation & Integrasi
*(Jika tim 3 orang, bagian ini digabung dengan Mahasiswa 1 & 2)*
- [ ] Buat menu XML untuk Bottom Navigation (`res/menu/bottom_nav_menu.xml`) dengan 3 item:
  - Dashboard
  - Task List
  - History
- [ ] Tambahkan `BottomNavigationView` dan `FragmentContainerView` (atau FrameLayout) pada `activity_main.xml`.
- [ ] Buat class Fragment:
  - [ ] `DashboardFragment.java`
  - [ ] `TaskListFragment.java`
  - [ ] `HistoryFragment.java`
- [ ] Implementasi navigasi fragment di `MainActivity`:
  - [ ] Transaksi fragment via `FragmentManager` & `FragmentTransaction` saat item menu dipilih.
- [ ] Implementasi `TaskAdapter` (RecyclerView Adapter):
  - [ ] `ViewHolder` pattern untuk rendering daftar tugas di `TaskListFragment`.
  - [ ] Event click listener (misal: centang selesai untuk memindahkan tugas ke `HistoryFragment`).
- [ ] Integrasi kontrol Focus Timer di `DashboardFragment` dengan `FocusTimerService`.
- [ ] Pengujian integrasi seluruh alur dari input tugas hingga timer berjalan.

---

## 🗓️ Checklist Berdasarkan Milestones (Lini Masa)

### 🚀 Minggu 1: Perencanaan & Dasar UI
- [ ] **Setup Proyek:**
  - [ ] Inisialisasi project Android Studio baru (Java, Empty Views Activity).
  - [ ] Setup repository Git (GitHub/GitLab) untuk kolaborasi tim.
  - [ ] Konfigurasi `build.gradle` (dependensi Material Components, RecyclerView).
- [ ] **Desain & UI Controls:**
  - [ ] Buat layout `activity_main.xml` (BottomNav + container).
  - [ ] Buat layout `activity_add_task.xml` dengan validasi visual.
  - [ ] Buat layout 3 Fragment (`Dashboard`, `TaskList`, `History`).
  - [ ] Buat layout item `item_task.xml`.

---

### ⚙️ Minggu 2: Fungsionalitas Inti
- [ ] **Navigasi & Intent:**
  - [ ] Navigasi Bottom Navigation antar 3 Fragment berfungsi mulus.
  - [ ] Panggil `AddTaskActivity` dari `TaskListFragment` via Explicit Intent.
  - [ ] Form input dengan `TimePicker` dan validasi anti-crash berfungsi normal.
  - [ ] Kembalikan data tugas baru dan tampilkan di `RecyclerView`.
- [ ] **Service & Background Process:**
  - [ ] `FocusTimerService` berhasil dijalankan dan dihentikan.
  - [ ] Hitung mundur timer tetap aktif saat berpindah ke tab lain (misal dari Dashboard ke History).
- [ ] **Manajemen Data Sederhana:**
  - [ ] Penyimpanan data in-memory (misal: Singleton / static repository) atau `SharedPreferences` agar data tersimpan selama sesi aplikasi.

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
