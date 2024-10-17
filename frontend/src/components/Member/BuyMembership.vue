<template>
  <div>
    <Navbar />
    <div class="container">
      <h1>Buy Membership</h1>
      <p>Welcome to your Buy Membership page!</p>

      <!-- Membership Plans Section -->
      <div class="card-container">
        <div class="card">
          <h2>Rp.100.000</h2>
          <h3>Paket 1 bulan</h3>
          <p>
            Coba layanan kami dengan akses penuh ke semua kelas selama satu
            bulan. Cocok untuk yang ingin eksplorasi dan melihat apakah program
            ini sesuai kebutuhan.
          </p>
          <button class="select-plan-button">Pilih Plan</button>
        </div>

        <div class="card">
          <h2>Rp.200.000</h2>
          <h3>Paket 2 bulan</h3>
          <p>
            Dapatkan akses penuh selama dua bulan untuk menjelajahi lebih banyak
            kelas.
          </p>
          <button class="select-plan-button">Pilih Plan</button>
        </div>

        <div class="card">
          <h2>Rp.300.000</h2>
          <h3>Paket 3 bulan</h3>
          <p>
            Paket ini memberi Anda akses penuh selama tiga bulan untuk
            memaksimalkan pengalaman belajar Anda.
          </p>
          <button class="select-plan-button">Pilih Plan</button>
        </div>

        <div class="card">
          <h2>Rp.400.000</h2>
          <h3>Paket 6 bulan</h3>
          <p>
            Paket yang sempurna bagi Anda yang ingin berkomitmen dalam waktu
            yang lebih lama.
          </p>
          <button class="select-plan-button">Pilih Plan</button>
        </div>
      </div>

      <!-- Visit Package Section -->
      <div class="visit-package">
        <h2>Beli Paket Visit</h2>
        <div class="visit-card">
          <h2>50.000</h2>
          <h3>Paket Visit</h3>
          <p>
            Paket visit Kelas menawarkan kesempatan bagi siapa saja untuk
            mencoba atau meningkatkan keterampilan bela diri. Peserta akan
            mendapatkan akses ke fasilitas lengkap dan program yang fleksibel
            sesuai jadwal mereka.
          </p>
          <button class="select-visit-button">Beli Paket Visit</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import Navbar from '../Navbar.vue' // Import the Navbar component

export default {
  name: 'BuyMembership',
  components: {
    Navbar,
  },
}
</script>

<style>
.container {
  padding: 20px;
}

.card-container {
  display: flex;
  justify-content: space-between;
  flex-wrap: wrap; /* Allow cards to wrap in case of small screens */
}

.card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 20px;
  margin: 10px;
  flex: 1 1 calc(25% - 20px); /* 4 cards in a row with space */
  box-shadow: 2px 2px 12px rgba(0, 0, 0, 0.1);
  text-align: center;
}

.select-plan-button,
.select-visit-button {
  background-color: #557c56; /* Your button color */
  color: #ffe5cf; /* Your text color */
  border: none;
  border-radius: 5px;
  padding: 10px 15px;
  cursor: pointer;
  font-size: 16px;
}

.visit-package {
  margin-top: 40px;
}

.visit-card {
  border: 1px solid #ccc;
  border-radius: 8px;
  padding: 20px;
  text-align: center;
  box-shadow: 2px 2px 12px rgba(0, 0, 0, 0.1);
}
</style>
