# RentAgri - Agricultural Equipment Rental and Marketplace System

RentAgri is a modern Android application that combines agricultural equipment rental, marketplace, and financial tracking in a single platform.

## 🌾 Features

### 📱 Main Modules
- **Equipment Rental**: Rent out and rent agricultural machinery
- **Marketplace**: Listing of agricultural product prices
- **Financial Tracking**: Income and expense records and reporting
- **User Management**: Profile, listings, and offer tracking
- **Weather Information**: Current weather data for agricultural activities

### 🔧 Technical Features
- **Modern UI**: Intuitive user interface with Jetpack Compose
- **Secure Authentication**: Firebase Authentication
- **Real-time Data**: Firebase Firestore integration
- **Local Storage**: Room Persistence Library
- **Photo Upload**: Firebase Storage support
- **Turkey Geography**: Province/district-based location selection

## 🛠️ Technology Stack

- **Programming Language**: Kotlin
- **UI Framework**: Jetpack Compose
- **Architecture**: MVVM (Model-View-ViewModel)
- **Asynchronous Operations**: Kotlin Coroutines
- **Dependency Injection**: Hilt
- **Backend**: Firebase (Firestore, Storage, Authentication)
- **Local Database**: Room
- **Network Communication**: Retrofit + OkHttp

## 📲 Installation

### Requirements
- Android Studio Hedgehog (2023.1.1) or later
- JDK 17
- Android SDK 24 (minimum) - 34 (target)

### Installation Steps
1. Clone the repository:
   ```bash
   git clone https://github.com/bestamibakir/RentAgri.git
   ```

2. Open the project in Android Studio

3. Firebase configuration:
   - Create a new project in Firebase Console
   - Add the `google-services.json` file to the `app/` folder
   - Enable Firestore Database and Storage

4. Build and run the project

## 🏗️ Project Structure

```
app/src/main/java/com/bestamibakir/rentagri/
├── data/
│   ├── api/           # API services
│   ├── database/      # Room database
│   ├── model/         # Data models
│   └── repository/    # Repository classes
├── di/                # Hilt modules
├── ui/
│   ├── components/    # Reusable UI components
│   ├── navigation/    # Navigation definitions
│   ├── screens/       # Application screens
│   └── theme/         # UI theme and styles
└── utils/             # Utility classes
```

## 📊 Screenshots

<img width="346" height="731" alt="splash-screen" src="https://github.com/user-attachments/assets/badf8630-742f-4651-883a-a513fc3353a8" />
<img width="346" height="731" alt="kayit" src="https://github.com/user-attachments/assets/6d8db74f-bf12-4172-8a43-99f107a19d2e" />
<img width="346" height="731" alt="kayit-bos" src="https://github.com/user-attachments/assets/44e840b2-c161-4b83-b4c5-9577b5ebc2cc" />
<img width="346" height="731" alt="kayit-kontrol" src="https://github.com/user-attachments/assets/2a219049-1c35-4c50-bdd4-7ab22de06eb7" />
<img width="346" height="731" alt="kayit-il-secimi" src="https://github.com/user-attachments/assets/871d6e48-abd5-4a74-acc4-63ed64595d09" />
<img width="346" height="731" alt="kayit-ilce-secimi" src="https://github.com/user-attachments/assets/630c4449-a1a1-4016-9232-173b342ef441" />
<img width="346" height="731" alt="giris" src="https://github.com/user-attachments/assets/29e94d98-f30d-43ad-823d-2c56b5b44d6a" />
<img width="346" height="731" alt="home-screen" src="https://github.com/user-attachments/assets/22bc4dfc-8c58-4d35-a293-762bf983128e" />
<img width="346" height="731" alt="kiralik-ana" src="https://github.com/user-attachments/assets/b4ea603b-428c-41c1-b9e2-6686bb7277bc" />
<img width="346" height="731" alt="ilan-olustur-filtre-bos" src="https://github.com/user-attachments/assets/87aae1c1-ed41-49bf-be82-fb5e0a5755d4" />
<img width="346" height="731" alt="ilan-olustur-filtre" src="https://github.com/user-attachments/assets/4bbbbe68-e256-4821-98da-be7b060317d7" />
<img width="346" height="731" alt="kiralik-filtreli" src="https://github.com/user-attachments/assets/77dae7a6-5322-4698-9364-8f04424c609a" />
<img width="346" height="731" alt="ilan-olustur-izinsiz" src="https://github.com/user-attachments/assets/38010809-d593-43f5-9d54-1a6518deadbd" />
<img width="346" height="731" alt="ilan-olustur-izinler-1" src="https://github.com/user-attachments/assets/e6afc129-f405-4257-8682-b88247eab89f" />
<img width="346" height="731" alt="ilan-olustur-izinler-2" src="https://github.com/user-attachments/assets/fbf28154-a961-4fcc-b25c-333d656b129b" />
<img width="346" height="731" alt="ilan-olustur-fotografli" src="https://github.com/user-attachments/assets/e3d370ec-9163-42d7-a329-3eb3570d7b90" />
<img width="346" height="731" alt="ilan-detay" src="https://github.com/user-attachments/assets/a34fd617-9c91-4696-a354-204c4d5bffd8" />
<img width="346" height="731" alt="ilan-detay-teklif" src="https://github.com/user-attachments/assets/7b622ba3-1f95-4e2a-a6f3-41cc32d99f70" />
<img width="346" height="731" alt="gelir-gider-ana" src="https://github.com/user-attachments/assets/4b63de00-3af2-4c9a-919e-32259fd4abdf" />
<img width="346" height="731" alt="gelirler" src="https://github.com/user-attachments/assets/5d003a89-73fd-41ea-a32a-3c0df3e23b25" />
<img width="346" height="731" alt="giderler" src="https://github.com/user-attachments/assets/a5325bf1-d833-41ad-8691-55d7a6f7cf35" />
<img width="346" height="731" alt="islem-ekleme" src="https://github.com/user-attachments/assets/63091dc0-d0be-4b72-b696-73294bdb35b0" />
<img width="346" height="731" alt="kategori-ekleme" src="https://github.com/user-attachments/assets/e40e9f03-09a7-4082-8ea9-a178faa17625" />
<img width="346" height="731" alt="islem-guncelleme" src="https://github.com/user-attachments/assets/254c0a4c-3fff-4be2-8f52-1a54d346181e" />
<img width="346" height="731" alt="gunluk-rapor-1" src="https://github.com/user-attachments/assets/3b1b8ae9-40a0-42e8-85c3-ad763d027b46" />
<img width="346" height="731" alt="gunluk-rapor-2" src="https://github.com/user-attachments/assets/09e13498-79f4-4f0c-8dbe-b35d5c5ecce5" />
<img width="346" height="731" alt="haftalik-rapor" src="https://github.com/user-attachments/assets/673b5145-7e0d-4dc2-8d8f-31ff443f5a97" />
<img width="346" height="731" alt="aylik-rapor" src="https://github.com/user-attachments/assets/6d533fbe-961f-488b-be0a-67a35018cf63" />
<img width="346" height="731" alt="aylik-rapor-devam" src="https://github.com/user-attachments/assets/446f991d-4e68-42a5-979d-f2a4296b0b62" />
<img width="346" height="731" alt="borsa-alf" src="https://github.com/user-attachments/assets/56b9b096-4a11-4252-8f41-967e54e41ead" />
<img width="346" height="731" alt="borsa-art" src="https://github.com/user-attachments/assets/ffaf55db-2b71-4ea0-9a3b-cabb0896e421" />
<img width="346" height="731" alt="borsa-azl" src="https://github.com/user-attachments/assets/b8720b10-c6f5-48eb-b0c2-b0b164ebbddc" />
<img width="346" height="731" alt="borsa-arama" src="https://github.com/user-attachments/assets/30ce7738-9bb9-453a-aa24-67cd5a40a5c9" />
<img width="346" height="731" alt="profil" src="https://github.com/user-attachments/assets/22260fe8-c9e5-4dde-b3f3-9cc54399f2d7" />
<img width="346" height="731" alt="profil-gelen-teklifler" src="https://github.com/user-attachments/assets/95766cb4-af04-4cfb-9f4d-603e05871150" />
<img width="346" height="731" alt="profil-gelen-teklifler-2" src="https://github.com/user-attachments/assets/224c2faf-f90f-4157-9f22-c1534ca3e4e9" /
<img width="346" height="731" alt="profil-verdigim-teklifler" src="https://github.com/user-attachments/assets/951b3fb3-c3bc-464b-8fcc-2ce43ae2c750" />
<img width="346" height="731" alt="profil-verdigim-teklifler-2" src="https://github.com/user-attachments/assets/4160d7cb-b3ea-47b3-bb74-6376766c8a91" />
<img width="346" height="731" alt="profil-verdigim-teklifler-3" src="https://github.com/user-attachments/assets/6123d2eb-ed32-4097-866b-df41e9d640bc" />
<img width="346" height="731" alt="profil-guncelleme" src="https://github.com/user-attachments/assets/30122d88-e484-47df-9e32-9887dc8d0bef" />
<img width="346" height="731" alt="cikis" src="https://github.com/user-attachments/assets/642c6c89-6cc3-441d-80d7-0fae660aa49e" />

## 🚀 Usage

1. **Account Creation**: Open the app and create a new account
2. **Post Listing**: Use "Post Listing" from the main menu to list your equipment
3. **Equipment Search**: Find the equipment you need in the "Listings" section
4. **Make Offer**: Make offers on listings you like
5. **Financial Tracking**: Record your income and expenses in the "Financial" section

## 🤝 Contributing

1. Fork this repository
2. Create a feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Create a Pull Request

## 📄 License

This project is licensed under the [MIT License](LICENSE).

## 👨‍💻 Developer

**Bestami BAKIR**
- GitHub: [@bestamibakir](https://github.com/bestamibakir)
- Email: bestamibakir@gmail.com

## 🙏 Acknowledgments

This project was developed using the powerful capabilities of the Jetpack Compose and Firebase ecosystem. It aims to contribute to digital transformation in the agricultural sector.

---

⭐ If you like this project, please give it a star to show your support! 
