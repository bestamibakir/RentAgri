package com.bestamibakir.rentagri.data.model

import java.text.Collator
import java.util.Locale

data class Province(
    val code: String,
    val name: String,
    val districts: List<String>
)

object TurkeyData {
    val provinces = listOf(
        Province(
            code = "01",
            name = "Adana",
            districts = listOf(
                "Aladağ", "Ceyhan", "Çukurova", "Feke", "İmamoğlu", "Karaisalı",
                "Karataş", "Kozan", "Pozantı", "Saimbeyli", "Sarıçam", "Seyhan",
                "Tufanbeyli", "Yumurtalık", "Yüreğir"
            )
        ),
        Province(
            code = "02",
            name = "Adıyaman",
            districts = listOf(
                "Besni", "Çelikhan", "Gerger", "Gölbaşı", "Kahta", "Merkez",
                "Samsat", "Sincik", "Tut"
            )
        ),
        Province(
            code = "03",
            name = "Afyonkarahisar",
            districts = listOf(
                "Bolvadin", "Çay", "Çobanlar", "Dazkırı", "Dinar", "Emirdağ",
                "Evciler", "Hocalar", "İhsaniye", "İscehisar", "Kızılören",
                "Merkez", "Sandıklı", "Sinanpaşa", "Sultandağı", "Şuhut"
            )
        ),
        Province(
            code = "04",
            name = "Ağrı",
            districts = listOf(
                "Diyadin", "Doğubayazıt", "Eleşkirt", "Hamur", "Merkez",
                "Patnos", "Taşlıçay", "Tutak"
            )
        ),
        Province(
            code = "05",
            name = "Amasya",
            districts = listOf(
                "Göynücek", "Gümüşhacıköy", "Hamamözü", "Merkez", "Merzifon",
                "Suluova", "Taşova"
            )
        ),
        Province(
            code = "06",
            name = "Ankara",
            districts = listOf(
                "Akyurt", "Altındağ", "Ayaş", "Bala", "Beypazarı", "Çamlıdere",
                "Çankaya", "Çubuk", "Elmadağ", "Etimesgut", "Evren", "Gölbaşı",
                "Güdül", "Haymana", "Kalecik", "Kazan", "Keçiören", "Kızılcahamam",
                "Mamak", "Nallıhan", "Polatlı", "Pursaklar", "Sincan", "Şereflikoçhisar",
                "Yenimahalle"
            )
        ),
        Province(
            code = "07",
            name = "Antalya",
            districts = listOf(
                "Akseki", "Aksu", "Alanya", "Demre", "Döşemealtı", "Elmalı",
                "Finike", "Gazipaşa", "Gündoğmuş", "İbradı", "Kaş", "Kemer",
                "Kepez", "Konyaaltı", "Korkuteli", "Kumluca", "Manavgat",
                "Muratpaşa", "Serik"
            )
        ),
        Province(
            code = "08",
            name = "Artvin",
            districts = listOf(
                "Ardanuç", "Arhavi", "Borçka", "Hopa", "Merkez", "Murgul",
                "Şavşat", "Yusufeli"
            )
        ),
        Province(
            code = "09",
            name = "Aydın",
            districts = listOf(
                "Bozdoğan", "Buharkent", "Çine", "Didim", "Efeler", "Germencik",
                "İncirliova", "Karacasu", "Karpuzlu", "Koçarlı", "Köşk",
                "Kuşadası", "Kuyucak", "Nazilli", "Söke", "Sultanhisar", "Yenipazar"
            )
        ),
        Province(
            code = "10",
            name = "Balıkesir",
            districts = listOf(
                "Altıeylül", "Ayvalık", "Balya", "Bandırma", "Bigadiç", "Burhaniye",
                "Dursunbey", "Edremit", "Erdek", "Gömeç", "Gönen", "Havran",
                "İvrindi", "Karesi", "Kepsut", "Manyas", "Marmara", "Savaştepe",
                "Sındırgı", "Susurluk"
            )
        ),
        Province(
            code = "11",
            name = "Bilecik",
            districts = listOf(
                "Bozüyük", "Gölpazarı", "İnhisar", "Merkez", "Osmaneli",
                "Pazaryeri", "Söğüt", "Yenipazar"
            )
        ),
        Province(
            code = "12",
            name = "Bingöl",
            districts = listOf(
                "Adaklı", "Genç", "Karlıova", "Kiğı", "Merkez", "Solhan",
                "Yayladere", "Yedisu"
            )
        ),
        Province(
            code = "13",
            name = "Bitlis",
            districts = listOf(
                "Adilcevaz", "Ahlat", "Güroymak", "Hizan", "Merkez", "Mutki",
                "Tatvan"
            )
        ),
        Province(
            code = "14",
            name = "Bolu",
            districts = listOf(
                "Dörtdivan", "Gerede", "Göynük", "Kıbrıscık", "Mengen", "Merkez",
                "Mudurnu", "Seben", "Yeniçağa"
            )
        ),
        Province(
            code = "15",
            name = "Burdur",
            districts = listOf(
                "Ağlasun", "Altınyayla", "Bucak", "Çavdır", "Çeltikçi", "Gölhisar",
                "Karamanlı", "Kemer", "Merkez", "Tefenni", "Yeşilova"
            )
        ),
        Province(
            code = "16",
            name = "Bursa",
            districts = listOf(
                "Büyükorhan", "Gemlik", "Gürsu", "Harmancık", "İnegöl", "İznik",
                "Karacabey", "Keles", "Kestel", "Mudanya", "Mustafakemalpaşa",
                "Nilüfer", "Orhaneli", "Orhangazi", "Osmangazi", "Yenişehir",
                "Yıldırım"
            )
        ),
        Province(
            code = "17",
            name = "Çanakkale",
            districts = listOf(
                "Ayvacık", "Bayramiç", "Biga", "Bozcaada", "Çan", "Eceabat",
                "Ezine", "Gelibolu", "Gökçeada", "Lapseki", "Merkez", "Yenice"
            )
        ),
        Province(
            code = "18",
            name = "Çankırı",
            districts = listOf(
                "Atkaracalar", "Bayramören", "Çerkeş", "Eldivan", "Ilgaz",
                "Kızılırmak", "Korgun", "Kurşunlu", "Merkez", "Orta", "Şabanözü", "Yapraklı"
            )
        ),
        Province(
            code = "19",
            name = "Çorum",
            districts = listOf(
                "Alaca", "Bayat", "Boğazkale", "Dodurga", "İskilip", "Kargı",
                "Laçin", "Mecitözü", "Merkez", "Oğuzlar", "Ortaköy", "Osmancık",
                "Sungurlu", "Uğurludağ"
            )
        ),
        Province(
            code = "20",
            name = "Denizli",
            districts = listOf(
                "Acıpayam", "Babadağ", "Baklan", "Bekilli", "Beyağaç", "Bozkurt",
                "Buldan", "Çal", "Çameli", "Çardak", "Çivril", "Güney",
                "Honaz", "Kale", "Merkezefendi", "Pamukkale", "Sarayköy",
                "Serinhisar", "Tavas"
            )
        ),
        Province(
            code = "21",
            name = "Diyarbakır",
            districts = listOf(
                "Bağlar", "Bismil", "Çermik", "Çınar", "Çüngüş", "Dicle",
                "Eğil", "Ergani", "Hani", "Hazro", "Kayapınar", "Kocaköy",
                "Kulp", "Lice", "Silvan", "Sur", "Yenişehir"
            )
        ),
        Province(
            code = "22",
            name = "Edirne",
            districts = listOf(
                "Enez", "Havsa", "İpsala", "Keşan", "Lalapaşa", "Meriç",
                "Merkez", "Süloğlu", "Uzunköprü"
            )
        ),
        Province(
            code = "23",
            name = "Elazığ",
            districts = listOf(
                "Ağın", "Alacakaya", "Arıcak", "Baskil", "Karakoçan", "Keban",
                "Kovancılar", "Maden", "Merkez", "Palu", "Sivrice"
            )
        ),
        Province(
            code = "24",
            name = "Erzincan",
            districts = listOf(
                "Çayırlı", "İliç", "Kemah", "Kemaliye", "Merkez", "Otlukbeli",
                "Refahiye", "Tercan", "Üzümlü"
            )
        ),
        Province(
            code = "25",
            name = "Erzurum",
            districts = listOf(
                "Aşkale", "Aziziye", "Çat", "Hınıs", "Horasan", "İspir",
                "Karaçoban", "Karayazı", "Köprüköy", "Narman", "Oltu",
                "Olur", "Palandöken", "Pasinler", "Pazaryolu", "Şenkaya",
                "Tekman", "Tortum", "Uzundere", "Yakutiye"
            )
        ),
        Province(
            code = "26",
            name = "Eskişehir",
            districts = listOf(
                "Alpu", "Beylikova", "Çifteler", "Günyüzü", "Han", "İnönü",
                "Mahmudiye", "Mihalgazi", "Mihalıççık", "Odunpazarı", "Sarıcakaya",
                "Seyitgazi", "Sivrihisar", "Tepebaşı"
            )
        ),
        Province(
            code = "27",
            name = "Gaziantep",
            districts = listOf(
                "Araban", "İslahiye", "Karkamış", "Nizip", "Nurdağı", "Oğuzeli",
                "Şahinbey", "Şehitkamil", "Yavuzeli"
            )
        ),
        Province(
            code = "28",
            name = "Giresun",
            districts = listOf(
                "Alucra", "Bulancak", "Çamoluk", "Çanakçı", "Dereli", "Doğankent",
                "Espiye", "Eynesil", "Görele", "Güce", "Keşap", "Merkez",
                "Piraziz", "Şebinkarahisar", "Tirebolu", "Yağlıdere"
            )
        ),
        Province(
            code = "29",
            name = "Gümüşhane",
            districts = listOf(
                "Kelkit", "Köse", "Kürtün", "Merkez", "Şiran", "Torul"
            )
        ),
        Province(
            code = "30",
            name = "Hakkari",
            districts = listOf(
                "Çukurca", "Derecik", "Merkez", "Şemdinli", "Yüksekova"
            )
        ),
        Province(
            code = "31",
            name = "Hatay",
            districts = listOf(
                "Altınözü", "Antakya", "Arsuz", "Belen", "Defne", "Dörtyol",
                "Erzin", "Hassa", "İskenderun", "Kırıkhan", "Kumlu", "Payas",
                "Reyhanlı", "Samandağ", "Yayladağı"
            )
        ),
        Province(
            code = "32",
            name = "Isparta",
            districts = listOf(
                "Aksu", "Atabey", "Eğirdir", "Gelendost", "Gönen", "Keçiborlu",
                "Merkez", "Senirkent", "Sütçüler", "Şarkikaraağaç", "Uluborlu",
                "Yalvaç", "Yenişarbademli"
            )
        ),
        Province(
            code = "33",
            name = "Mersin",
            districts = listOf(
                "Akdeniz", "Anamur", "Aydıncık", "Bozyazı", "Çamlıyayla",
                "Erdemli", "Gülnar", "Mezitli", "Mut", "Silifke", "Tarsus",
                "Toroslar", "Yenişehir"
            )
        ),
        Province(
            code = "34",
            name = "İstanbul Avrupa",
            districts = listOf(
                "Adalar", "Arnavutköy", "Avcılar", "Bağcılar", "Bahçelievler",
                "Bakırköy", "Başakşehir", "Bayrampaşa", "Beşiktaş", "Beylikdüzü",
                "Beyoğlu", "Büyükçekmece", "Çatalca", "Esenler", "Esenyurt",
                "Eyüpsultan", "Fatih", "Gaziosmanpaşa", "Güngören", "Kağıthane",
                "Küçükçekmece", "Sarıyer", "Silivri", "Sultangazi", "Şişli",
                "Zeytinburnu"
            )
        ),
        Province(
            code = "34",
            name = "İstanbul Asya",
            districts = listOf(
                "Ataşehir", "Beykoz", "Çekmeköy", "Kadıköy", "Kartal",
                "Maltepe", "Pendik", "Sancaktepe", "Sultanbeyli", "Şile",
                "Tuzla", "Ümraniye", "Üsküdar"
            )
        ),
        Province(
            code = "35",
            name = "İzmir",
            districts = listOf(
                "Aliağa", "Balçova", "Bayındır", "Bayraklı", "Bergama", "Beydağ",
                "Bornova", "Buca", "Çeşme", "Çiğli", "Dikili", "Foça",
                "Gaziemir", "Güzelbahçe", "Karabağlar", "Karaburun", "Karşıyaka",
                "Kemalpaşa", "Kınık", "Kiraz", "Konak", "Menderes", "Menemen",
                "Narlıdere", "Ödemiş", "Seferihisar", "Selçuk", "Tire", "Torbalı", "Urla"
            )
        ),
        Province(
            code = "36",
            name = "Kars",
            districts = listOf(
                "Akyaka", "Arpaçay", "Digor", "Kağızman", "Merkez", "Sarıkamış",
                "Selim", "Susuz"
            )
        ),
        Province(
            code = "37",
            name = "Kastamonu",
            districts = listOf(
                "Abana", "Ağlı", "Araç", "Azdavay", "Bozkurt", "Cide",
                "Çatalzeytin", "Daday", "Devrekani", "Doğanyurt", "Hanönü",
                "İhsangazi", "İnebolu", "Küre", "Merkez", "Pınarbaşı", "Seydiler",
                "Şenpazar", "Taşköprü", "Tosya"
            )
        ),
        Province(
            code = "38",
            name = "Kayseri",
            districts = listOf(
                "Akkışla", "Bünyan", "Develi", "Felahiye", "Hacılar", "İncesu",
                "Kocasinan", "Melikgazi", "Özvatan", "Pınarbaşı", "Sarıoğlan",
                "Sarız", "Talas", "Tomarza", "Yahyalı", "Yeşilhisar"
            )
        ),
        Province(
            code = "39",
            name = "Kırklareli",
            districts = listOf(
                "Babaeski", "Demirköy", "Kofçaz", "Lüleburgaz", "Merkez", "Pehlivanköy",
                "Pınarhisar", "Vize"
            )
        ),
        Province(
            code = "40",
            name = "Kırşehir",
            districts = listOf(
                "Akçakent", "Akpınar", "Boztepe", "Çiçekdağı", "Kaman", "Merkez",
                "Mucur"
            )
        ),
        Province(
            code = "41",
            name = "Kocaeli",
            districts = listOf(
                "Başiskele", "Çayırova", "Darıca", "Derince", "Dilovası", "Gebze",
                "Gölcük", "İzmit", "Kandıra", "Karamürsel", "Kartepe", "Körfez"
            )
        ),
        Province(
            code = "42",
            name = "Konya",
            districts = listOf(
                "Ahırlı", "Akören", "Akşehir", "Altınekin", "Beyşehir", "Bozkır",
                "Cihanbeyli", "Çeltik", "Çumra", "Derbent", "Derebucak", "Doğanhisar",
                "Emirgazi", "Ereğli", "Güneysinir", "Hadim", "Halkapınar", "Hüyük",
                "Ilgın", "Kadınhanı", "Karapınar", "Karatay", "Kulu", "Meram",
                "Sarayönü", "Selçuklu", "Seydişehir", "Taşkent", "Tuzlukçu", "Yalıhüyük",
                "Yunak"
            )
        ),
        Province(
            code = "43",
            name = "Kütahya",
            districts = listOf(
                "Altıntaş", "Aslanapa", "Çavdarhisar", "Domaniç", "Dumlupınar",
                "Emet", "Gediz", "Hisarcık", "Merkez", "Pazarlar", "Simav",
                "Şaphane", "Tavşanlı"
            )
        ),
        Province(
            code = "44",
            name = "Malatya",
            districts = listOf(
                "Akçadağ", "Arapgir", "Arguvan", "Battalgazi", "Darende", "Doğanşehir",
                "Doğanyol", "Hekimhan", "Kale", "Kuluncak", "Pütürge", "Yazıhan", "Yeşilyurt"
            )
        ),
        Province(
            code = "45",
            name = "Manisa",
            districts = listOf(
                "Ahmetli", "Akhisar", "Alaşehir", "Demirci", "Gölmarmara", "Gördes",
                "Kırkağaç", "Köprübaşı", "Kula", "Salihli", "Sarıgöl", "Saruhanlı",
                "Selendi", "Soma", "Şehzadeler", "Turgutlu", "Yunusemre"
            )
        ),
        Province(
            code = "46",
            name = "Kahramanmaraş",
            districts = listOf(
                "Afşin", "Andırın", "Çağlayancerit", "Dulkadiroğlu", "Ekinözü",
                "Elbistan", "Göksun", "Nurhak", "Onikişubat", "Pazarcık", "Türkoğlu"
            )
        ),
        Province(
            code = "47",
            name = "Mardin",
            districts = listOf(
                "Artuklu", "Dargeçit", "Derik", "Kızıltepe", "Mazıdağı", "Midyat",
                "Nusaybin", "Ömerli", "Savur", "Yeşilli"
            )
        ),
        Province(
            code = "48",
            name = "Muğla",
            districts = listOf(
                "Bodrum", "Dalaman", "Datça", "Fethiye", "Kavaklıdere", "Köyceğiz",
                "Marmaris", "Menteşe", "Milas", "Ortaca", "Seydikemer", "Ula", "Yatağan"
            )
        ),
        Province(
            code = "49",
            name = "Muş",
            districts = listOf(
                "Bulanık", "Hasköy", "Korkut", "Malazgirt", "Merkez", "Varto"
            )
        ),
        Province(
            code = "50",
            name = "Nevşehir",
            districts = listOf(
                "Acıgöl", "Avanos", "Derinkuyu", "Gülşehir", "Hacıbektaş", "Kozaklı",
                "Merkez", "Ürgüp"
            )
        ),
        Province(
            code = "51",
            name = "Niğde",
            districts = listOf(
                "Altunhisar", "Bor", "Çamardı", "Çiftlik", "Merkez", "Ulukışla"
            )
        ),
        Province(
            code = "52",
            name = "Ordu",
            districts = listOf(
                "Akkuş", "Altınordu", "Aybastı", "Çamaş", "Çatalpınar", "Çaybaşı",
                "Fatsa", "Gölköy", "Gülyalı", "Gürgentepe", "İkizce", "Kabadüz",
                "Kabataş", "Korgan", "Kumru", "Mesudiye", "Perşembe", "Ulubey", "Ünye"
            )
        ),
        Province(
            code = "53",
            name = "Rize",
            districts = listOf(
                "Ardeşen", "Çamlıhemşin", "Çayeli", "Derepazarı", "Fındıklı",
                "Güneysu", "Hemşin", "İkizdere", "İyidere", "Kalkandere", "Merkez", "Pazar"
            )
        ),
        Province(
            code = "54",
            name = "Sakarya",
            districts = listOf(
                "Adapazarı", "Akyazı", "Arifiye", "Erenler", "Ferizli", "Geyve",
                "Hendek", "Karapürçek", "Karasu", "Kaynarca", "Kocaali", "Pamukova",
                "Sapanca", "Serdivan", "Söğütlü", "Taraklı"
            )
        ),
        Province(
            code = "55",
            name = "Samsun",
            districts = listOf(
                "19 Mayıs", "Alaçam", "Asarcık", "Atakum", "Ayvacık", "Bafra",
                "Canik", "Çarşamba", "Havza", "İlkadım", "Kavak", "Ladik",
                "Ondokuzmayıs", "Salıpazarı", "Tekkeköy", "Terme", "Vezirköprü", "Yakakent"
            )
        ),
        Province(
            code = "56",
            name = "Siirt",
            districts = listOf(
                "Baykan", "Eruh", "Kurtalan", "Merkez", "Pervari", "Şirvan", "Tillo"
            )
        ),
        Province(
            code = "57",
            name = "Sinop",
            districts = listOf(
                "Ayancık", "Boyabat", "Dikmen", "Durağan", "Erfelek", "Gerze",
                "Merkez", "Saraydüzü", "Türkeli"
            )
        ),
        Province(
            code = "58",
            name = "Sivas",
            districts = listOf(
                "Akıncılar", "Altınyayla", "Divriği", "Doğanşar", "Gemerek",
                "Gölova", "Gürün", "Hafik", "İmranlı", "Kangal", "Koyulhisar",
                "Merkez", "Suşehri", "Şarkışla", "Ulaş", "Yıldızeli", "Zara"
            )
        ),
        Province(
            code = "59",
            name = "Tekirdağ",
            districts = listOf(
                "Çerkezköy", "Çorlu", "Ergene", "Hayrabolu", "Kapaklı", "Malkara",
                "Marmaraereğlisi", "Muratlı", "Saray", "Süleymanpaşa", "Şarköy"
            )
        ),
        Province(
            code = "60",
            name = "Tokat",
            districts = listOf(
                "Almus", "Artova", "Başçiftlik", "Erbaa", "Merkez", "Niksar",
                "Pazar", "Reşadiye", "Sulusaray", "Turhal", "Yeşilyurt", "Zile"
            )
        ),
        Province(
            code = "61",
            name = "Trabzon",
            districts = listOf(
                "Akçaabat", "Araklı", "Arsin", "Beşikdüzü", "Çarşıbaşı", "Çaykara",
                "Dernekpazarı", "Düzköy", "Hayrat", "Köprübaşı", "Maçka", "Of",
                "Ortahisar", "Şalpazarı", "Sürmene", "Tonya", "Vakfıkebir", "Yomra"
            )
        ),
        Province(
            code = "62",
            name = "Tunceli",
            districts = listOf(
                "Çemişgezek", "Hozat", "Mazgirt", "Merkez", "Nazımiye", "Ovacık",
                "Pertek", "Pülümür"
            )
        ),
        Province(
            code = "63",
            name = "Şanlıurfa",
            districts = listOf(
                "Akçakale", "Birecik", "Bozova", "Ceylanpınar", "Eyyübiye", "Halfeti",
                "Haliliye", "Harran", "Hilvan", "Karaköprü", "Siverek", "Suruç", "Viranşehir"
            )
        ),
        Province(
            code = "64",
            name = "Uşak",
            districts = listOf(
                "Banaz", "Eşme", "Karahallı", "Merkez", "Sivaslı", "Ulubey"
            )
        ),
        Province(
            code = "65",
            name = "Van",
            districts = listOf(
                "Bahçesaray", "Başkale", "Çaldıran", "Çatak", "Edremit", "Erciş",
                "Gevaş", "Gürpınar", "İpekyolu", "Muradiye", "Özalp", "Saray", "Tuşba"
            )
        ),
        Province(
            code = "66",
            name = "Yozgat",
            districts = listOf(
                "Akdağmadeni", "Aydıncık", "Boğazlıyan", "Çandır", "Çayıralan",
                "Çekerek", "Kadışehri", "Merkez", "Saraykent", "Sarıkaya", "Sorgun",
                "Şefaatli", "Yenifakılı", "Yerköy"
            )
        ),
        Province(
            code = "67",
            name = "Zonguldak",
            districts = listOf(
                "Alaplı", "Çaycuma", "Devrek", "Gökçebey", "Kilimli", "Kozlu", "Merkez", "Ereğli"
            )
        ),
        Province(
            code = "68",
            name = "Aksaray",
            districts = listOf(
                "Ağaçören", "Eskil", "Gülağaç", "Güzelyurt", "Merkez", "Ortaköy", "Sarıyahşi"
            )
        ),
        Province(
            code = "69",
            name = "Bayburt",
            districts = listOf(
                "Aydıntepe", "Demirözü", "Merkez"
            )
        ),
        Province(
            code = "70",
            name = "Karaman",
            districts = listOf(
                "Ayrancı", "Başyayla", "Ermenek", "Kazımkarabekir", "Merkez", "Sarıveliler"
            )
        ),
        Province(
            code = "71",
            name = "Kırıkkale",
            districts = listOf(
                "Bahşili", "Balışeyh", "Çelebi", "Delice", "Karakeçili", "Keskin",
                "Merkez", "Sulakyurt", "Yahşihan"
            )
        ),
        Province(
            code = "72",
            name = "Batman",
            districts = listOf(
                "Beşiri", "Gercüş", "Hasankeyf", "Kozluk", "Merkez", "Sason"
            )
        ),
        Province(
            code = "73",
            name = "Şırnak",
            districts = listOf(
                "Beytüşşebap", "Cizre", "Güçlükonak", "İdil", "Merkez", "Silopi", "Uludere"
            )
        ),
        Province(
            code = "74",
            name = "Bartın",
            districts = listOf(
                "Amasra", "Kurucaşile", "Merkez", "Ulus"
            )
        ),
        Province(
            code = "75",
            name = "Ardahan",
            districts = listOf(
                "Çıldır", "Damal", "Göle", "Hanak", "Merkez", "Posof"
            )
        ),
        Province(
            code = "76",
            name = "Iğdır",
            districts = listOf(
                "Aralık", "Karakoyunlu", "Merkez", "Tuzluca"
            )
        ),
        Province(
            code = "77",
            name = "Yalova",
            districts = listOf(
                "Altınova", "Armutlu", "Çiftlikköy", "Çınarcık", "Merkez", "Termal"
            )
        ),
        Province(
            code = "78",
            name = "Karabük",
            districts = listOf(
                "Eflani", "Eskipazar", "Merkez", "Ovacık", "Safranbolu", "Yenice"
            )
        ),
        Province(
            code = "79",
            name = "Kilis",
            districts = listOf(
                "Elbeyli", "Merkez", "Musabeyli", "Polateli"
            )
        ),
        Province(
            code = "80",
            name = "Osmaniye",
            districts = listOf(
                "Bahçe", "Düziçi", "Hasanbeyli", "Kadirli", "Merkez", "Sumbas", "Toprakkale"
            )
        ),
        Province(
            code = "81",
            name = "Düzce",
            districts = listOf(
                "Akçakoca",
                "Cumayeri",
                "Çilimli",
                "Gölyaka",
                "Gümüşova",
                "Kaynaşlı",
                "Merkez",
                "Yığılca"
            )
        )
    )

    fun getProvinceByCode(code: String): Province? {
        return provinces.find { it.code == code }
    }

    fun getProvinceByName(name: String): Province? {
        return provinces.find { it.name.equals(name, ignoreCase = true) }
    }

    fun getDistrictsByProvince(provinceName: String): List<String> {
        val turkishCollator = Collator.getInstance(Locale("tr", "TR"))
        return getProvinceByName(provinceName)?.districts?.sortedWith(turkishCollator)
            ?: emptyList()
    }

    fun getAllProvinceNames(): List<String> {
        val turkishCollator = Collator.getInstance(Locale("tr", "TR"))
        return provinces.map { it.name }.sortedWith(turkishCollator)
    }
} 