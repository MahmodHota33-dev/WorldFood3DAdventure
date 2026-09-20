import * as THREE from "three";


/* =========================================================
   TRANSLATIONS DICTIONARY (i18n)
========================================================= */
const translations = {
    en: {
        meta_title: "WorldFood 3D Adventure | Travel · Food · Match-3",
        nav_home: "Home",
        nav_adventure: "Adventure",
        nav_world: "World",
        nav_food: "Food",
        nav_gameplay: "Gameplay",
        nav_features: "Features",
        nav_about: "About",
        nav_release: "Release",
        cta_play_soon: "Play Soon",
        hero_badge: "WORLD FOOD · MATCH-3 · TRAVEL ADVENTURE",
        hero_title_1: "Travel the world.",
        hero_title_2: "Discover the food.",
        hero_title_3: "Master the puzzle.",
        hero_desc: "Explore 213 destinations in a premium Match-3 travel adventure. Discover iconic foods, collect stars, unlock new countries and master 3,195 handcrafted puzzle levels.",
        hero_btn_explore: "Explore Adventure",
        hero_btn_gameplay: "View Gameplay",
        stat_levels: "LEVELS",
        stat_countries: "COUNTRIES",
        stat_per_country: "PER COUNTRY",
        stat_ads: "ADS",
        globe_loading: "LOADING WORLD",
        globe_drag: "DRAG THE GLOBE",
        card_world_tour: "WORLD TOUR",
        country_germany: "GERMANY",
        card_level: "LEVEL",
        card_level_10: "LEVEL 10",
        card_moves: "MOVES",
        card_goal: "GOAL",
        adv_label: "THE ADVENTURE",
        adv_title_1: "One journey.",
        adv_title_2: "A world of discovery.",
        adv_desc: "World exploration, international food and handcrafted Match-3 gameplay come together in one connected adventure.",
        adv_card1_title: "Explore the World",
        adv_card1_desc: "Travel across the world, collect stars and unlock new destinations as your adventure grows.",
        adv_card1_link: "World exploration →",
        adv_card2_title: "Discover Food",
        adv_card2_desc: "Discover food inspired by countries and cultures from around the world.",
        adv_card2_link: "Discover dishes →",
        adv_card3_title: "Master Match-3",
        adv_card3_desc: "Create special tiles, trigger chain reactions and master increasingly challenging objectives.",
        adv_card3_link: "Master puzzles →",
        countries_label: "WORLD TOUR",
        countries_title_1: "213 destinations.",
        countries_title_2: "One adventure.",
        countries_desc: "Begin in Europe and continue across the world while unlocking new chapters through progression.",
        ch_1: "CHAPTER 01",
        ch_2: "CHAPTER 02",
        ch_3: "CHAPTER 03",
        ch_4: "CHAPTER 04",
        ch_5: "CHAPTER 05",
        ch_6: "CHAPTER 06",
        ch_7: "CHAPTER 07 · FINALE",
        country_germany_desc: "Begin the journey with Germany's first fifteen handcrafted challenges.",
        country_italy: "Italy",
        country_italy_desc: "Continue through Italy with colorful food themes and new objectives.",
        country_france: "France",
        country_france_desc: "Explore a French chapter with distinctive cuisine and new Match-3 challenges.",
        country_spain: "Spain",
        country_spain_desc: "Enter a vibrant Mediterranean chapter inspired by Spanish food and atmosphere.",
        country_japan: "Japan",
        country_japan_desc: "Discover a refined Japanese chapter with recognizable cuisine and new challenges.",
        country_mexico: "Mexico",
        country_mexico_desc: "Experience a colorful destination filled with energy and food-inspired puzzles.",
        country_sudan: "Sudan",
        country_sudan_desc: "Complete Chapter 1 of the WorldFood journey with the final fifteen challenges.",
        levels_15: "15 Levels",
        food_pretzel: "Pretzel",
        food_pizza: "Pizza",
        food_croissant: "Croissant",
        food_paella: "Paella",
        food_sushi: "Sushi",
        food_tacos: "Tacos",
        food_ful: "Ful Medames",
        food_section_label: "SIGNATURE FOODS",
        food_title_1: "Discover food",
        food_title_2: "around the world.",
        food_section_desc: "Every destination brings its own culinary identity into the WorldFood adventure.",
        fc_germany: "🇩🇪 GERMANY",
        fc_italy: "🇮🇹 ITALY",
        fc_france: "🇫🇷 FRANCE",
        fc_spain: "🇪🇸 SPAIN",
        fc_japan: "🇯🇵 JAPAN",
        fc_mexico: "🇲🇽 MEXICO",
        fc_sudan: "🇸🇩 SUDAN",
        food_pretzel_desc: "A recognizable baked classic from German food culture.",
        food_pizza_desc: "One of Italy's most famous and colorful dishes.",
        food_croissant_desc: "An iconic French favorite with an unmistakable shape.",
        food_paella_desc: "A colorful Spanish dish inspired by Mediterranean cuisine.",
        food_sushi_desc: "Clean shapes and vivid colors inspired by Japanese cuisine.",
        food_tacos_desc: "A colorful favorite bringing energy to the world tour.",
        food_ful_desc: "A traditional dish completing the culinary journey of Chapter 1.",
        gp_section_label: "MATCH-3 GAMEPLAY",
        gp_title_1: "Every move can",
        gp_title_2: "change the board.",
        gp_section_desc: "Complete objectives within limited moves, combine food tiles and create powerful special pieces.",
        mech_1_title: "Match Three",
        mech_1_desc: "Connect matching tiles to clear the board.",
        mech_2_title: "Line Clears",
        mech_2_desc: "Four-tile matches create row or column clears.",
        mech_3_title: "Bombs",
        mech_3_desc: "T and L matches create explosive special tiles.",
        mech_4_title: "Color Bomb",
        mech_4_desc: "Five-tile matches create powerful color bombs.",
        booster_hammer: "Hammer",
        booster_shuffle: "Shuffle",
        booster_moves: "Moves",
        feat_section_label: "GAME FEATURES",
        feat_title_1: "Built for a",
        feat_title_2: "premium adventure.",
        feat_1_title: "3D World Globe",
        feat_1_desc: "Explore destinations through a world-focused adventure.",
        feat_2_title: "3,195 Levels",
        feat_2_desc: "213 countries with fifteen handcrafted challenges each.",
        feat_3_title: "Special Tiles",
        feat_3_desc: "Line clears, bombs and powerful color bombs.",
        feat_4_title: "Star Progression",
        feat_4_desc: "Earn stars and unlock new destinations.",
        feat_5_title: "Rewards",
        feat_5_desc: "Track milestones, progress and rewards.",
        feat_6_title: "Passport",
        feat_6_desc: "Record completed destinations throughout the world tour.",
        feat_7_title: "Music & Sound",
        feat_7_desc: "Audio and haptics bring the adventure to life.",
        feat_8_title: "No Ads",
        feat_8_desc: "Adventure without advertising interruptions.",
        tech_section_label: "BUILT FOR ANDROID",
        tech_title_1: "Modern technology.",
        tech_title_2: "Native experience.",
        tech_section_desc: "WorldFood 3D Adventure is built as a native Android application.",
        tech_lang: "Language",
        tech_ui: "User Interface",
        tech_cloud: "Cloud Services",
        tech_async: "Async Runtime",
        about_section_label: "ABOUT THE GAME",
        about_title_1: "More than",
        about_title_2: "a puzzle game.",
        about_desc_1: "WorldFood 3D Adventure combines world exploration, international food, progression and Match-3 gameplay.",
        about_desc_2: "Every country becomes another chapter in a larger global adventure.",
        about_github_link: "Explore the GitHub repository →",
        proj_world_food: "WORLD FOOD",
        proj_active: "ACTIVE PROJECT",
        proj_subtitle: "Premium Match-3 Travel Adventure",
        proj_platform: "PLATFORM",
        proj_language: "LANGUAGE",
        proj_ui_label: "UI",
        proj_world_label: "WORLD",
        rel_title_1: "Ready for the",
        rel_title_2: "adventure?",
        rel_desc: "Travel the world. Discover the food. Master the puzzle.",
        rel_version: "Version 1.0.0",
        rel_levels_count: "3,195 Levels",
        rel_btn_github: "View Project on GitHub",
        footer_tagline: "Travel the World · Discover Food · Master the Puzzle",
        footer_explore: "EXPLORE",
        footer_game: "GAME",
        footer_project: "PROJECT",
        footer_privacy: "Privacy Policy",
        footer_rights: "WorldFood 3D Adventure. All rights reserved.",
        footer_built: "Built with Kotlin · Jetpack Compose · Android"
    },
    de: {
        meta_title: "WorldFood 3D Adventure | Reisen · Essen · Match-3",
        nav_home: "Startseite",
        nav_adventure: "Abenteuer",
        nav_world: "Welt",
        nav_food: "Essen",
        nav_gameplay: "Gameplay",
        nav_features: "Features",
        nav_about: "Über das Spiel",
        nav_release: "Release",
        cta_play_soon: "Bald spielbar",
        hero_badge: "WELTESSEN · MATCH-3 · REISEABENTEUER",
        hero_title_1: "Bereise die Welt.",
        hero_title_2: "Entdecke das Essen.",
        hero_title_3: "Meistere das Puzzle.",
        hero_desc: "Erkunde 213 Reiseziele in einem erstklassigen Match-3-Reiseabenteuer. Entdecke ikonische Gerichte, sammle Sterne, schalte neue Länder frei und meistere 3.195 handgefertigte Puzzle-Level.",
        hero_btn_explore: "Abenteuer erkunden",
        hero_btn_gameplay: "Gameplay ansehen",
        stat_levels: "LEVEL",
        stat_countries: "LÄNDER",
        stat_per_country: "PRO LAND",
        stat_ads: "WERBUNG",
        globe_loading: "WELT LADEN",
        globe_drag: "WELTKUGEL DREHEN",
        card_world_tour: "WELTREISE",
        country_germany: "DEUTSCHLAND",
        card_level: "LEVEL",
        card_level_10: "LEVEL 10",
        card_moves: "ZÜGE",
        card_goal: "ZIEL",
        adv_label: "DAS ABENTEUER",
        adv_title_1: "Eine Reise.",
        adv_title_2: "Eine Welt voller Entdeckungen.",
        adv_desc: "Weltentdeckung, internationale Kulinarik und handgefertigtes Match-3-Gameplay verschmelzen zu einem zusammenhängenden Abenteuer.",
        adv_card1_title: "Die Welt erkunden",
        adv_card1_desc: "Bereise die Welt, sammle Sterne und schalte neue Ziele frei, während dein Abenteuer wächst.",
        adv_card1_link: "Welterkundung →",
        adv_card2_title: "Essen entdecken",
        adv_card2_desc: "Entdecke Gerichte, inspiriert von Ländern und Kulturen aus der ganzen Welt.",
        adv_card2_link: "Gerichte entdecken →",
        adv_card3_title: "Match-3 meistern",
        adv_card3_desc: "Erstelle Spezialsteine, löse Kettenreaktionen aus und meistere immer anspruchsvollere Ziele.",
        adv_card3_link: "Puzzles meistern →",
        countries_label: "WELTREISE",
        countries_title_1: "213 Reiseziele.",
        countries_title_2: "Ein Abenteuer.",
        countries_desc: "Beginne in Europa und reise weiter um die Welt, während du durch Fortschritt neue Kapitel freischaltest.",
        ch_1: "KAPITEL 01",
        ch_2: "KAPITEL 02",
        ch_3: "KAPITEL 03",
        ch_4: "KAPITEL 04",
        ch_5: "KAPITEL 05",
        ch_6: "KAPITEL 06",
        ch_7: "KAPITEL 07 · FINALE",
        country_germany_desc: "Beginne die Reise mit den ersten fünfzehn handgefertigten Herausforderungen Deutschlands.",
        country_italy: "Italien",
        country_italy_desc: "Reise weiter durch Italien mit farbenfrohen Food-Themen und neuen Zielen.",
        country_france: "Frankreich",
        country_france_desc: "Erkunde ein französisches Kapitel mit unverwechselbarer Küche und neuen Match-3-Herausforderungen.",
        country_spain: "Spanien",
        country_spain_desc: "Betrete ein lebendiges Mittelmeer-Kapitel, inspiriert von spanischer Küche und Atmosphäre.",
        country_japan: "Japan",
        country_japan_desc: "Entdecke ein raffiniertes japanisches Kapitel mit bekannter Küche und neuen Herausforderungen.",
        country_mexico: "Mexiko",
        country_mexico_desc: "Erlebe ein farbenfrohes Reiseziel voller Energie und lebensfroher Food-Puzzles.",
        country_sudan: "Sudan",
        country_sudan_desc: "Vollende Kapitel 1 der WorldFood-Reise mit den letzten fünfzehn Herausforderungen.",
        levels_15: "15 Level",
        food_pretzel: "Brezel",
        food_pizza: "Pizza",
        food_croissant: "Croissant",
        food_paella: "Paella",
        food_sushi: "Sushi",
        food_tacos: "Tacos",
        food_ful: "Ful Medames",
        food_section_label: "SIGNATUR-GERICHTE",
        food_title_1: "Entdecke Essen",
        food_title_2: "auf der ganzen Welt.",
        food_section_desc: "Jedes Reiseziel bringt seine eigene kulinarische Identität in das WorldFood-Abenteuer ein.",
        fc_germany: "🇩🇪 DEUTSCHLAND",
        fc_italy: "🇮🇹 ITALIEN",
        fc_france: "🇫🇷 FRANKREICH",
        fc_spain: "🇪🇸 SPANIEN",
        fc_japan: "🇯🇵 JAPAN",
        fc_mexico: "🇲🇽 MEXIKO",
        fc_sudan: "🇸🇩 SUDAN",
        food_pretzel_desc: "Ein erkennbarer Backwaren-Klassiker aus der deutschen Esskultur.",
        food_pizza_desc: "Eines der berühmtesten und farbenfrohesten Gerichte Italiens.",
        food_croissant_desc: "Ein ikonischer französischer Liebling mit unverkennbarer Form.",
        food_paella_desc: "Ein farbenfrohes spanisches Gericht, inspiriert von der mediterranen Küche.",
        food_sushi_desc: "Klare Formen und lebendige Farben, inspiriert von der japanischen Küche.",
        food_tacos_desc: "Ein farbenfroher Favorit, der Energie in die Welttournee bringt.",
        food_ful_desc: "Ein traditionelles Gericht, das die kulinarische Reise von Kapitel 1 vollendet.",
        gp_section_label: "MATCH-3 GAMEPLAY",
        gp_title_1: "Jeder Zug kann",
        gp_title_2: "das Spielfeld verändern.",
        gp_section_desc: "Erfülle Ziele in begrenzten Zügen, kombiniere Food-Steine und erschaffe mächtige Spezialteile.",
        mech_1_title: "3er-Kombination",
        mech_1_desc: "Verbinde passende Steine, um das Spielfeld zu leeren.",
        mech_2_title: "Reihen-Räumung",
        mech_2_desc: "Vierer-Kombinationen räumen ganze Reihen oder Spalten ab.",
        mech_3_title: "Bomben",
        mech_3_desc: "T- und L-Kombinationen erzeugen explosive Spezialsteine.",
        mech_4_title: "Farb-Bombe",
        mech_4_desc: "Fünfer-Kombinationen erschaffen mächtige Farbbomben.",
        booster_hammer: "Hammer",
        booster_shuffle: "Mischen",
        booster_moves: "Züge",
        feat_section_label: "SPIEL-FEATURES",
        feat_title_1: "Gemacht für ein",
        feat_title_2: "Premium-Abenteuer.",
        feat_1_title: "3D-Weltkugel",
        feat_1_desc: "Erkunde Reiseziele durch ein weltzentriertes Abenteuer.",
        feat_2_title: "3.195 Level",
        feat_2_desc: "213 Länder mit jeweils fünfzehn handgefertigten Herausforderungen.",
        feat_3_title: "Spezialsteine",
        feat_3_desc: "Reihenräumungen, Bomben und mächtige Farbbomben.",
        feat_4_title: "Sternen-Fortschritt",
        feat_4_desc: "Verdiene Sterne und schalte neue Reiseziele frei.",
        feat_5_title: "Belohnungen",
        feat_5_desc: "Verfolge Meilensteine, Fortschritte und Belohnungen.",
        feat_6_title: "Reisepass",
        feat_6_desc: "Zeichne abgeschlossene Reiseziele auf der Welttournee auf.",
        feat_7_title: "Musik & Sound",
        feat_7_desc: "Audio und Haptik erwecken das Abenteuer zum Leben.",
        feat_8_title: "Keine Werbung",
        feat_8_desc: "Abenteuer ohne Werbeunterbrechungen.",
        tech_section_label: "FÜR ANDROID ENTWICKELT",
        tech_title_1: "Moderne Technologie.",
        tech_title_2: "Natives Erlebnis.",
        tech_section_desc: "WorldFood 3D Adventure ist als native Android-Anwendung gebaut.",
        tech_lang: "Sprache",
        tech_ui: "Benutzeroberfläche",
        tech_cloud: "Cloud-Dienste",
        tech_async: "Async Runtime",
        about_section_label: "ÜBER DAS SPIEL",
        about_title_1: "Mehr als",
        about_title_2: "ein Puzzle-Spiel.",
        about_desc_1: "WorldFood 3D Adventure verbindet Welterkundung, internationales Essen, Fortschritt und Match-3-Gameplay.",
        about_desc_2: "Jedes Land wird zu einem weiteren Kapitel in einem größeren globalen Abenteuer.",
        about_github_link: "GitHub-Repository erkunden →",
        proj_world_food: "WORLD FOOD",
        proj_active: "AKTIVES PROJEKT",
        proj_subtitle: "Erstklassiges Match-3-Reiseabenteuer",
        proj_platform: "PLATTFORM",
        proj_language: "SPRACHE",
        proj_ui_label: "UI",
        proj_world_label: "WELT",
        rel_title_1: "Bereit für das",
        rel_title_2: "Abenteuer?",
        rel_desc: "Bereise die Welt. Entdecke das Essen. Meistere das Puzzle.",
        rel_version: "Version 1.0.0",
        rel_levels_count: "3.195 Level",
        rel_btn_github: "Projekt auf GitHub ansehen",
        footer_tagline: "Die Welt bereisen · Essen entdecken · Das Puzzle meistern",
        footer_explore: "ERKUNDEN",
        footer_game: "SPIEL",
        footer_project: "PROJEKT",
        footer_privacy: "Datenschutz",
        footer_rights: "WorldFood 3D Adventure. Alle Rechte vorbehalten.",
        footer_built: "Entwickelt mit Kotlin · Jetpack Compose · Android"
    },
    es: {
        meta_title: "WorldFood 3D Adventure | Viajes · Comida · Match-3",
        nav_home: "Inicio",
        nav_adventure: "Aventura",
        nav_world: "Mundo",
        nav_food: "Comida",
        nav_gameplay: "Jugabilidad",
        nav_features: "Características",
        nav_about: "Acerca de",
        nav_release: "Lanzamiento",
        cta_play_soon: "Pronto",
        hero_badge: "WORLD FOOD · MATCH-3 · AVENTURA DE VIAJES",
        hero_title_1: "Viaja por el mundo.",
        hero_title_2: "Descubre la comida.",
        hero_title_3: "Domina el puzle.",
        hero_desc: "Explora 213 destinos en una aventura de viajes Match-3 prémium. Descubre alimentos icónicos, colecciona estrellas, desbloquea nuevos países y domina 3,195 niveles de puzle hechos a mano.",
        hero_btn_explore: "Explorar Aventura",
        hero_btn_gameplay: "Ver Jugabilidad",
        stat_levels: "NIVELES",
        stat_countries: "PAÍSES",
        stat_per_country: "POR PAÍS",
        stat_ads: "ANUNCIOS",
        globe_loading: "CARGANDO MUNDO",
        globe_drag: "GIRA EL GLOBO",
        card_world_tour: "TOUR MUNDIAL",
        country_germany: "ALEMANIA",
        card_level: "NIVEL",
        card_level_10: "NIVEL 10",
        card_moves: "MOVIMIENTOS",
        card_goal: "OBJETIVO",
        adv_label: "LA AVENTURA",
        adv_title_1: "Un viaje.",
        adv_title_2: "Un mundo por descubrir.",
        adv_desc: "La exploración mundial, la comida internacional y la jugabilidad Match-3 hecha a mano se unen en una aventura conectada.",
        adv_card1_title: "Explora el Mundo",
        adv_card1_desc: "Viaja por el mundo, colecciona estrellas y desbloquea nuevos destinos a medida que crece tu aventura.",
        adv_card1_link: "Exploración mundial →",
        adv_card2_title: "Descubre Comida",
        adv_card2_desc: "Descubre comida inspirada en países y culturas de todo el mundo.",
        adv_card2_link: "Descubrir platos →",
        adv_card3_title: "Domina Match-3",
        adv_card3_desc: "Crea fichas especiales, activa reacciones en cadena y domina objetivos cada vez más desafiantes.",
        adv_card3_link: "Domina puzles →",
        countries_label: "TOUR MUNDIAL",
        countries_title_1: "213 destinos.",
        countries_title_2: "Una aventura.",
        countries_desc: "Comienza en Europa y continúa por el mundo mientras desbloqueas nuevos capítulos mediante la progresión.",
        ch_1: "CAPÍTULO 01",
        ch_2: "CAPÍTULO 02",
        ch_3: "CAPÍTULO 03",
        ch_4: "CAPÍTULO 04",
        ch_5: "CAPÍTULO 05",
        ch_6: "CAPÍTULO 06",
        ch_7: "CAPÍTULO 07 · FINAL",
        country_germany_desc: "Comienza el viaje con los primeros quince desafíos artesanales de Alemania.",
        country_italy: "Italia",
        country_italy_desc: "Continúa por Italia con coloridos temas gastronómicos y nuevos objetivos.",
        country_france: "Francia",
        country_france_desc: "Explora un capítulo francés con cocina distintiva y nuevos desafíos Match-3.",
        country_spain: "España",
        country_spain_desc: "Entra en un vibrante capítulo mediterráneo inspirado en la comida y atmósfera española.",
        country_japan: "Japón",
        country_japan_desc: "Descubre un capítulo japonés refinado con cocina reconocible y nuevos desafíos.",
        country_mexico: "México",
        country_mexico_desc: "Experimenta un destino colorido lleno de energía y puzles inspirados en la comida.",
        country_sudan: "Sudán",
        country_sudan_desc: "Completa el Capítulo 1 del viaje WorldFood con los últimos quince desafíos.",
        levels_15: "15 Niveles",
        food_pretzel: "Pretzel",
        food_pizza: "Pizza",
        food_croissant: "Croissant",
        food_paella: "Paella",
        food_sushi: "Sushi",
        food_tacos: "Tacos",
        food_ful: "Ful Medames",
        food_section_label: "COMIDAS TÍPICAS",
        food_title_1: "Descubre comida",
        food_title_2: "alrededor del mundo.",
        food_section_desc: "Cada destino aporta su propia identidad culinaria a la aventura de WorldFood.",
        fc_germany: "🇩🇪 ALEMANIA",
        fc_italy: "🇮🇹 ITALIA",
        fc_france: "🇫🇷 FRANCIA",
        fc_spain: "🇪🇸 ESPAÑA",
        fc_japan: "🇯🇵 JAPÓN",
        fc_mexico: "🇲🇽 MÉXICO",
        fc_sudan: "🇸🇩 SUDÁN",
        food_pretzel_desc: "Un clásico de la repostería reconocible de la cultura alemana.",
        food_pizza_desc: "Uno de los platos más famosos y coloridos de Italia.",
        food_croissant_desc: "Un favorito icónico francés con una forma inconfundible.",
        food_paella_desc: "Un plato español colorido inspirado en la cocina mediterránea.",
        food_sushi_desc: "Formas limpias y colores vivos inspirados en la cocina japonesa.",
        food_tacos_desc: "Un favorito colorido que aporta energía al tour mundial.",
        food_ful_desc: "Un plato tradicional que completa el viaje culinario del Capítulo 1.",
        gp_section_label: "JUGABILIDAD MATCH-3",
        gp_title_1: "Cada movimiento puede",
        gp_title_2: "cambiar el tablero.",
        gp_section_desc: "Completa objetivos en movimientos limitados, combina fichas de comida y crea piezas especiales poderosas.",
        mech_1_title: "Combinar Tres",
        mech_1_desc: "Conecta fichas coincidentes para limpiar el tablero.",
        mech_2_title: "Limpieza de Líneas",
        mech_2_desc: "Las combinaciones de cuatro fichas limpian filas o columnas.",
        mech_3_title: "Bombas",
        mech_3_desc: "Las combinaciones en T y L crean fichas especiales explosivas.",
        mech_4_title: "Bomba de Color",
        mech_4_desc: "Las combinaciones de cinco fichas crean bombas de color poderosas.",
        booster_hammer: "Martillo",
        booster_shuffle: "Mezclar",
        booster_moves: "Movimientos",
        feat_section_label: "CARACTERÍSTICAS DEL JUEGO",
        feat_title_1: "Creado para una",
        feat_title_2: "aventura prémium.",
        feat_1_title: "Globo terráqueo 3D",
        feat_1_desc: "Explora destinos a través de una aventura centrada en el mundo.",
        feat_2_title: "3,195 Niveles",
        feat_2_desc: "213 países con quince desafíos artesanales cada uno.",
        feat_3_title: "Fichas Especiales",
        feat_3_desc: "Limpiezas de línea, bombas y bombas de color potentes.",
        feat_4_title: "Progresión de Estrellas",
        feat_4_desc: "Gana estrellas y desbloquea nuevos destinos.",
        feat_5_title: "Recompensas",
        feat_5_desc: "Sigue hitos, progreso y recompensas.",
        feat_6_title: "Pasaporte",
        feat_6_desc: "Registra los destinos completados durante el tour mundial.",
        feat_7_title: "Música y Sonido",
        feat_7_desc: "El audio y la háptica dan vida a la aventura.",
        feat_8_title: "Sin Anuncios",
        feat_8_desc: "Aventura sin interrupciones publicitarias.",
        tech_section_label: "CREADO PARA ANDROID",
        tech_title_1: "Tecnología moderna.",
        tech_title_2: "Experiencia nativa.",
        tech_section_desc: "WorldFood 3D Adventure está construido como una aplicación nativa de Android.",
        tech_lang: "Idioma",
        tech_ui: "Interfaz de usuario",
        tech_cloud: "Servicios Cloud",
        tech_async: "Runtime Asíncrono",
        about_section_label: "ACERCA DEL JUEGO",
        about_title_1: "Más que",
        about_title_2: "un juego de puzles.",
        about_desc_1: "WorldFood 3D Adventure combina exploración mundial, comida internacional, progresión y jugabilidad Match-3.",
        about_desc_2: "Cada país se convierte en otro capítulo de una aventura global más grande.",
        about_github_link: "Explorar el repositorio de GitHub →",
        proj_world_food: "WORLD FOOD",
        proj_active: "PROYECTO ACTIVO",
        proj_subtitle: "Aventura de Viajes Match-3 Prémium",
        proj_platform: "PLATAFORMA",
        proj_language: "IDIOMA",
        proj_ui_label: "IU",
        proj_world_label: "MUNDO",
        rel_title_1: "¿Listo para la",
        rel_title_2: "aventura?",
        rel_desc: "Viaja por el mundo. Descubre la comida. Domina el puzle.",
        rel_version: "Versión 1.0.0",
        rel_levels_count: "3,195 Niveles",
        rel_btn_github: "Ver proyecto en GitHub",
        footer_tagline: "Viaja por el mundo · Descubre comida · Domina el puzle",
        footer_explore: "EXPLORAR",
        footer_game: "JUEGO",
        footer_project: "PROYECTO",
        footer_privacy: "Política de Privacidad",
        footer_rights: "WorldFood 3D Adventure. Todos los derechos reservados.",
        footer_built: "Creado con Kotlin · Jetpack Compose · Android"
    },
    it: {
        meta_title: "WorldFood 3D Adventure | Viaggi · Cibo · Match-3",
        nav_home: "Home",
        nav_adventure: "Avventura",
        nav_world: "Mondo",
        nav_food: "Cibo",
        nav_gameplay: "Gameplay",
        nav_features: "Caratteristiche",
        nav_about: "Informazioni",
        nav_release: "Uscita",
        cta_play_soon: "Presto in arrivo",
        hero_badge: "WORLD FOOD · MATCH-3 · AVVENTURA DI VIAGGIO",
        hero_title_1: "Viaggia nel mondo.",
        hero_title_2: "Scopri il cibo.",
        hero_title_3: "Domina il rompicapo.",
        hero_desc: "Esplora 213 destinazioni in un'avventura di viaggio Match-3 di livello superiore. Scopri cibi iconici, raccogli stelle, sblocca nuovi paesi e domina 3.195 livelli di puzzle fatti a mano.",
        hero_btn_explore: "Esplora l'Avventura",
        hero_btn_gameplay: "Guarda il Gameplay",
        stat_levels: "LIVELLI",
        stat_countries: "PAESI",
        stat_per_country: "PER PAESE",
        stat_ads: "PUBBLICITÀ",
        globe_loading: "CARICAMENTO MONDO",
        globe_drag: "TRASCINA IL GLOBO",
        card_world_tour: "TOUR MONDIALE",
        country_germany: "GERMANIA",
        card_level: "LIVELLO",
        card_level_10: "LIVELLO 10",
        card_moves: "MOSSE",
        card_goal: "OBIETTIVO",
        adv_label: "L'AVVENTURA",
        adv_title_1: "Un viaggio.",
        adv_title_2: "Un mondo di scoperte.",
        adv_desc: "L'esplorazione del mondo, il cibo internazionale e il gameplay Match-3 fatto a mano si uniscono in un'unica avventura.",
        adv_card1_title: "Esplora il Mondo",
        adv_card1_desc: "Viaggia per il mondo, raccogli stelle e sblocca nuove destinazioni mentre la tua avventura cresce.",
        adv_card1_link: "Esplorazione mondiale →",
        adv_card2_title: "Scopri il Cibo",
        adv_card2_desc: "Scopri cibi ispirati a paesi e culture di tutto il mondo.",
        adv_card2_link: "Scopri i piatti →",
        adv_card3_title: "Domina Match-3",
        adv_card3_desc: "Crea tessere speciali, attiva reazioni a catena e supera obiettivi sempre più impegnativi.",
        adv_card3_link: "Domina i puzzle →",
        countries_label: "TOUR MONDIALE",
        countries_title_1: "213 destinazioni.",
        countries_title_2: "Un'avventura.",
        countries_desc: "Inizia in Europa e continua in tutto il mondo sbloccando nuovi capitoli attraverso la progressione.",
        ch_1: "CAPITOLO 01",
        ch_2: "CAPITOLO 02",
        ch_3: "CAPITOLO 03",
        ch_4: "CAPITOLO 04",
        ch_5: "CAPITOLO 05",
        ch_6: "CAPITOLO 06",
        ch_7: "CAPITOLO 07 · FINALE",
        country_germany_desc: "Inizia il viaggio con le prime quindici sfide artigianali della Germania.",
        country_italy: "Italia",
        country_italy_desc: "Continua attraverso l'Italia con temi gastronomici colorati e nuovi obiettivi.",
        country_france: "Francia",
        country_france_desc: "Esplora un capitolo francese con cucina distintiva e nuove sfide Match-3.",
        country_spain: "Spagna",
        country_spain_desc: "Entra in un vibrante capitolo mediterraneo ispirato al cibo e all'atmosfera spagnola.",
        country_japan: "Giappone",
        country_japan_desc: "Scopri un raffinato capitolo giapponese con cucina riconoscibile e nuove sfide.",
        country_mexico: "Messico",
        country_mexico_desc: "Vivi una destinazione colorata piena di energia e puzzle ispirati al cibo.",
        country_sudan: "Sudan",
        country_sudan_desc: "Completa il Capitolo 1 del viaggio WorldFood con le ultime quindici sfide.",
        levels_15: "15 Livelli",
        food_pretzel: "Pretzel",
        food_pizza: "Pizza",
        food_croissant: "Croissant",
        food_paella: "Paella",
        food_sushi: "Sushi",
        food_tacos: "Tacos",
        food_ful: "Ful Medames",
        food_section_label: "CIBI TIPICI",
        food_title_1: "Scopri il cibo",
        food_title_2: "in tutto il mondo.",
        food_section_desc: "Ogni destinazione porta la propria identità culinaria nell'avventura di WorldFood.",
        fc_germany: "🇩🇪 GERMANIA",
        fc_italy: "🇮🇹 ITALIA",
        fc_france: "🇫🇷 FRANCIA",
        fc_spain: "🇪🇸 SPAGNA",
        fc_japan: "🇯🇵 GIAPPONE",
        fc_mexico: "🇲🇽 MESSICO",
        fc_sudan: "🇸🇩 SUDAN",
        food_pretzel_desc: "Un classico da forno riconoscibile della cultura alimentare tedesca.",
        food_pizza_desc: "Uno dei piatti più famosi e colorati d'Italia.",
        food_croissant_desc: "Un favorito francese iconico dalla forma inconfondibile.",
        food_paella_desc: "Un piatto spagnolo colorato ispirato alla cucina mediterranea.",
        food_sushi_desc: "Forme pulite e colori vividi ispirati alla cucina giapponese.",
        food_tacos_desc: "Un preferito colorato che porta energia al tour mondiale.",
        food_ful_desc: "Un piatto tradizionale che completa il viaggio culinario del Capitolo 1.",
        gp_section_label: "GAMEPLAY MATCH-3",
        gp_title_1: "Ogni mossa può",
        gp_title_2: "cambiare il tabellone.",
        gp_section_desc: "Completa gli obiettivi entro un numero limitato di mosse, combina le tessere di cibo e crea potenti pezzi speciali.",
        mech_1_title: "Abbina Tre",
        mech_1_desc: "Collega tessere corrispondenti per pulire il tabellone.",
        mech_2_title: "Eliminazione Linee",
        mech_2_desc: "Le combinazioni di quattro tessere puliscono righe o colonne.",
        mech_3_title: "Bombe",
        mech_3_desc: "Le combinazioni a T e L creano tessere speciali esplosive.",
        mech_4_title: "Bomba Colore",
        mech_4_desc: "Le combinazioni di cinque tessere creano potenti bombe di colore.",
        booster_hammer: "Martello",
        booster_shuffle: "Mescola",
        booster_moves: "Mosse",
        feat_section_label: "CARATTERISTICHE",
        feat_title_1: "Creato per un'",
        feat_title_2: "avventura superiore.",
        feat_1_title: "Globo Terrestre 3D",
        feat_1_desc: "Esplora le destinazioni attraverso un'avventura incentrata sul mondo.",
        feat_2_title: "3.195 Livelli",
        feat_2_desc: "213 paesi con quindici sfide artigianali ciascuno.",
        feat_3_title: "Tessere Speciali",
        feat_3_desc: "Eliminazioni di linee, bombe e potenti bombe di colore.",
        feat_4_title: "Progressione a Stelle",
        feat_4_desc: "Guadagna stelle e sblocca nuove destinazioni.",
        feat_5_title: "Ricompense",
        feat_5_desc: "Tieni traccia di traguardi, progressi e ricompense.",
        feat_6_title: "Passaporto",
        feat_6_desc: "Registra le destinazioni completate durante il tour mondiale.",
        feat_7_title: "Musica e Suoni",
        feat_7_desc: "Audio e aptica danno vita all'avventura.",
        feat_8_title: "Senza Pubblicità",
        feat_8_desc: "Avventura senza interruzioni pubblicitarie.",
        tech_section_label: "CREATO PER ANDROID",
        tech_title_1: "Tecnologia moderna.",
        tech_title_2: "Esperienza nativa.",
        tech_section_desc: "WorldFood 3D Adventure è costruito come applicazione Android nativa.",
        tech_lang: "Lingua",
        tech_ui: "Interfaccia utente",
        tech_cloud: "Servizi Cloud",
        tech_async: "Runtime Asincrono",
        about_section_label: "INFORMAZIONI SUL GIOCO",
        about_title_1: "Più che",
        about_title_2: "un gioco di puzzle.",
        about_desc_1: "WorldFood 3D Adventure combina esplorazione del mondo, cibo internazionale, progressione e gameplay Match-3.",
        about_desc_2: "Ogni paese diventa un altro capitolo di un'avventura globale più ampia.",
        about_github_link: "Esplora il repository GitHub →",
        proj_world_food: "WORLD FOOD",
        proj_active: "PROGETTO ATTIVO",
        proj_subtitle: "Avventura di Viaggio Match-3 Prémium",
        proj_platform: "PIATTAFORMA",
        proj_language: "LINGUA",
        proj_ui_label: "UI",
        proj_world_label: "MONDO",
        rel_title_1: "Pronto per l'",
        rel_title_2: "avventura?",
        rel_desc: "Viaggia nel mondo. Scopri il cibo. Domina il rompicapo.",
        rel_version: "Versione 1.0.0",
        rel_levels_count: "3.195 Livelli",
        rel_btn_github: "Visualizza progetto su GitHub",
        footer_tagline: "Viaggia nel mondo · Scopri il cibo · Domina il rompicapo",
        footer_explore: "ESPLORA",
        footer_game: "GIOCO",
        footer_project: "PROGETTO",
        footer_privacy: "Informativa sulla privacy",
        footer_rights: "WorldFood 3D Adventure. Tutti i diritti riservati.",
        footer_built: "Creato con Kotlin · Jetpack Compose · Android"
    },
    fr: {
        meta_title: "WorldFood 3D Adventure | Voyage · Cuisine · Match-3",
        nav_home: "Accueil",
        nav_adventure: "Aventure",
        nav_world: "Monde",
        nav_food: "Cuisine",
        nav_gameplay: "Gameplay",
        nav_features: "Fonctionnalités",
        nav_about: "À propos",
        nav_release: "Sortie",
        cta_play_soon: "Bientôt disponible",
        hero_badge: "WORLD FOOD · MATCH-3 · AVENTURE DE VOYAGE",
        hero_title_1: "Voyagez à travers le monde.",
        hero_title_2: "Découvrez la cuisine.",
        hero_title_3: "Maîtrisez le casse-tête.",
        hero_desc: "Explorez 213 destinations dans une aventure de voyage Match-3 haut de gamme. Découvrez des plats emblématiques, collectez des étoiles, débloquez de nouveaux pays et maîtrisez 3 195 niveaux de puzzle faits main.",
        hero_btn_explore: "Explorer l'aventure",
        hero_btn_gameplay: "Voir le gameplay",
        stat_levels: "NIVEAUX",
        stat_countries: "PAYS",
        stat_per_country: "PAR PAYS",
        stat_ads: "PUBS",
        globe_loading: "CHARGEMENT DU MONDE",
        globe_drag: "FAIRE PIVOTER LE GLOBE",
        card_world_tour: "TOUR DU MONDE",
        country_germany: "ALLEMAGNE",
        card_level: "NIVEAU",
        card_level_10: "NIVEAU 10",
        card_moves: "COUPS",
        card_goal: "OBJECTIF",
        adv_label: "L'AVENTURE",
        adv_title_1: "Un voyage.",
        adv_title_2: "Un monde de découvertes.",
        adv_desc: "L'exploration du monde, la cuisine internationale et le gameplay Match-3 fait main se réunissent en une seule aventure.",
        adv_card1_title: "Explorer le monde",
        adv_card1_desc: "Voyagez à travers le monde, collectez des étoiles et débloquez de nouvelles destinations au fil de votre aventure.",
        adv_card1_link: "Exploration mondiale →",
        adv_card2_title: "Découvrir la cuisine",
        adv_card2_desc: "Découvrez des plats inspirés de pays et de cultures du monde entier.",
        adv_card2_link: "Découvrir les plats →",
        adv_card3_title: "Maîtriser le Match-3",
        adv_card3_desc: "Créez des tuiles spéciales, déclenchez des réactions en chaîne et maîtrisez des objectifs de plus en plus stimulants.",
        adv_card3_link: "Maîtriser les puzzles →",
        countries_label: "TOUR DU MONDE",
        countries_title_1: "213 destinations.",
        countries_title_2: "Une aventure.",
        countries_desc: "Commencez en Europe et continuez à travers le monde tout en débloquant de nouveaux chapitres par la progression.",
        ch_1: "CHAPITRE 01",
        ch_2: "CHAPITRE 02",
        ch_3: "CHAPITRE 03",
        ch_4: "CHAPITRE 04",
        ch_5: "CHAPITRE 05",
        ch_6: "CHAPITRE 06",
        ch_7: "CHAPITRE 07 · FINALE",
        country_germany_desc: "Commencez le voyage avec les quinze premiers défis faits main de l'Allemagne.",
        country_italy: "Italie",
        country_italy_desc: "Continuez à travers l'Italie avec des thèmes culinaires colorés et de nouveaux objectifs.",
        country_france: "France",
        country_france_desc: "Explorez un chapitre français doté d'une cuisine distinctive et de nouveaux défis Match-3.",
        country_spain: "Espagne",
        country_spain_desc: "Entrez dans un chapitre méditerranéen dynamique inspiré par la cuisine et l'atmosphère espagnoles.",
        country_japan: "Japon",
        country_japan_desc: "Découvrez un chapitre japonais raffiné avec une cuisine reconnaissable et de nouveaux défis.",
        country_mexico: "Mexique",
        country_mexico_desc: "Vivez l'expérience d'une destination colorée pleine d'énergie et de puzzles inspirés de la nourriture.",
        country_sudan: "Soudan",
        country_sudan_desc: "Terminez le chapitre 1 du voyage WorldFood avec les quinze derniers défis.",
        levels_15: "15 Niveaux",
        food_pretzel: "Pretzel",
        food_pizza: "Pizza",
        food_croissant: "Croissant",
        food_paella: "Paella",
        food_sushi: "Sushi",
        food_tacos: "Tacos",
        food_ful: "Ful Medames",
        food_section_label: "SPÉCIALITÉS CULINAIRES",
        food_title_1: "Découvrez la cuisine",
        food_title_2: "du monde entier.",
        food_section_desc: "Chaque destination apporte sa propre identité culinaire dans l'aventure WorldFood.",
        fc_germany: "🇩🇪 ALLEMAGNE",
        fc_italy: "🇮🇹 ITALIE",
        fc_france: "🇫🇷 FRANCE",
        fc_spain: "🇪🇸 ESPAGNE",
        fc_japan: "🇯🇵 JAPON",
        fc_mexico: "🇲🇽 MEXIQUE",
        fc_sudan: "🇸🇩 SOUDAN",
        food_pretzel_desc: "Un classique de la boulangerie reconnaissable de la culture culinaire allemande.",
        food_pizza_desc: "L'un des plats les plus célèbres et colorés d'Italie.",
        food_croissant_desc: "Un incontournable français à la forme incomparable.",
        food_paella_desc: "Un plat espagnol coloré inspiré de la cuisine méditerranéenne.",
        food_sushi_desc: "Des formes épurées et des couleurs vives inspirées de la cuisine japonaise.",
        food_tacos_desc: "Un favori coloré apportant de l'énergie au tour du monde.",
        food_ful_desc: "Un plat traditionnel qui parachève le voyage culinaire du Chapitre 1.",
        gp_section_label: "GAMEPLAY MATCH-3",
        gp_title_1: "Chaque mouvement peut",
        gp_title_2: "changer le plateau.",
        gp_section_desc: "Atteignez vos objectifs en un nombre limité de mouvements, combinez des tuiles de nourriture et créez de puissantes pièces spéciales.",
        mech_1_title: "Associer Trois",
        mech_1_desc: "Connectez des tuiles correspondantes pour vider le plateau.",
        mech_2_title: "Lignes de suppression",
        mech_2_desc: "Les correspondances de quatre tuiles effacent des lignes ou des colonnes.",
        mech_3_title: "Bombes",
        mech_3_desc: "Les correspondances en T et en L créent des tuiles spéciales explosives.",
        mech_4_title: "Bombe Couleur",
        mech_4_desc: "Les correspondances de cinq tuiles créent de puissantes bombes de couleur.",
        booster_hammer: "Marteau",
        booster_shuffle: "Mélanger",
        booster_moves: "Mouvements",
        feat_section_label: "FONCTIONNALITÉS",
        feat_title_1: "Conçu pour une",
        feat_title_2: "aventure haut de gamme.",
        feat_1_title: "Globe 3D",
        feat_1_desc: "Explorez des destinations à travers une aventure centrée sur le monde.",
        feat_2_title: "3 195 Niveaux",
        feat_2_desc: "213 pays avec quinze défis faits main chacun.",
        feat_3_title: "Tuiles Spéciales",
        feat_3_desc: "Effacements de lignes, bombes et puissantes bombes de couleur.",
        feat_4_title: "Progression par Étoiles",
        feat_4_desc: "Gagnez des étoiles et débloquez de nouvelles destinations.",
        feat_5_title: "Récompenses",
        feat_5_desc: "Suivez jalons, progrès et récompenses.",
        feat_6_title: "Passeport",
        feat_6_desc: "Enregistrez les destinations complétées tout au long du tour du monde.",
        feat_7_title: "Musique & Son",
        feat_7_desc: "L'audio et le retour haptique donnent vie à l'aventure.",
        feat_8_title: "Sans Publicité",
        feat_8_desc: "Une aventure sans interruptions publicitaires.",
        tech_section_label: "CONÇU POUR ANDROID",
        tech_title_1: "Technologie moderne.",
        tech_title_2: "Expérience native.",
        tech_section_desc: "WorldFood 3D Adventure est conçu comme une application Android native.",
        tech_lang: "Langue",
        tech_ui: "Interface utilisateur",
        tech_cloud: "Services Cloud",
        tech_async: "Runtime Asynchrone",
        about_section_label: "À PROPOS DU JEU",
        about_title_1: "Plus qu'un",
        about_title_2: "simple jeu de puzzle.",
        about_desc_1: "WorldFood 3D Adventure combine exploration du monde, cuisine internationale, progression et gameplay Match-3.",
        about_desc_2: "Chaque pays devient un nouveau chapitre d'une aventure mondiale plus vaste.",
        about_github_link: "Explorer le dépôt GitHub →",
        proj_world_food: "WORLD FOOD",
        proj_active: "PROJET ACTIF",
        proj_subtitle: "Aventure de voyage Match-3 haut de gamme",
        proj_platform: "PLATEFORME",
        proj_language: "LANGUE",
        proj_ui_label: "UI",
        proj_world_label: "MONDE",
        rel_title_1: "Prêt pour l'",
        rel_title_2: "aventure ?",
        rel_desc: "Voyagez à travers le monde. Découvrez la cuisine. Maîtrisez le casse-tête.",
        rel_version: "Version 1.0.0",
        rel_levels_count: "3 195 Niveaux",
        rel_btn_github: "Voir le projet sur GitHub",
        footer_tagline: "Voyager dans le monde · Découvrir la cuisine · Maîtriser le puzzle",
        footer_explore: "EXPLORER",
        footer_game: "JEU",
        footer_project: "PROJET",
        footer_privacy: "Politique de confidentialité",
        footer_rights: "WorldFood 3D Adventure. Tous droits réservés.",
        footer_built: "Créé avec Kotlin · Jetpack Compose · Android"
    },
    ja: {
        meta_title: "WorldFood 3D Adventure | 旅行 · グルメ · マッチ3",
        nav_home: "ホーム",
        nav_adventure: "アドベンチャー",
        nav_world: "世界",
        nav_food: "グルメ",
        nav_gameplay: "ゲームプレイ",
        nav_features: "特徴",
        nav_about: "概要",
        nav_release: "リリース",
        cta_play_soon: "まもなく配信",
        hero_badge: "ワールドフード · マッチ3 · トラベルアドベンチャー",
        hero_title_1: "世界を旅しよう。",
        hero_title_2: "グルメを発見しよう。",
        hero_title_3: "パズルを極めよう。",
        hero_desc: "プレミアムなマッチ3トラベルアドベンチャーで213の目的地を探索しよう。象徴的な料理を発見し、星を集め、新しい国をアンロックし、3,195の手作りのパズルレベルをマスターしよう。",
        hero_btn_explore: "アドベンチャーを探索",
        hero_btn_gameplay: "ゲームプレイを見る",
        stat_levels: "レベル",
        stat_countries: "国・地域",
        stat_per_country: "1カ国あたり",
        stat_ads: "広告なし",
        globe_loading: "ワールド読み込み中",
        globe_drag: "地球儀をドラッグ",
        card_world_tour: "ワールドツアー",
        country_germany: "ドイツ",
        card_level: "レベル",
        card_level_10: "レベル 10",
        card_moves: "手数",
        card_goal: "目標",
        adv_label: "アドベンチャー",
        adv_title_1: "ひとつの旅。",
        adv_title_2: "発見に満ちた世界。",
        adv_desc: "世界の探索、国際色豊かなグルメ、手作りのマッチ3ゲームプレイがひとつにつながったアドベンチャー。",
        adv_card1_title: "世界を探索",
        adv_card1_desc: "世界中を旅し、星を集め、冒険の成長に合わせて新しい目的地をアンロックしよう。",
        adv_card1_link: "世界の探索 →",
        adv_card2_title: "グルメを発見",
        adv_card2_desc: "世界中の国や文化にインスパイアされた料理を発見しよう。",
        adv_card2_link: "料理を発見 →",
        adv_card3_title: "マッチ3をマスター",
        adv_card3_desc: "特殊タイルを作成し、連鎖反応を引き起こし、ますます難しくなる目標をクリアしよう。",
        adv_card3_link: "パズルをマスター →",
        countries_label: "ワールドツアー",
        countries_title_1: "213の目的地。",
        countries_title_2: "ひとつの冒険。",
        countries_desc: "ヨーロッパから始まり、進行に応じて新しいチャプターをアンロックしながら世界中を巡ろう。",
        ch_1: "チャプター 01",
        ch_2: "チャプター 02",
        ch_3: "チャプター 03",
        ch_4: "チャプター 04",
        ch_5: "チャプター 05",
        ch_6: "チャプター 06",
        ch_7: "チャプター 07 · フィナーレ",
        country_germany_desc: "ドイツの最初の15の手作りチャレンジから旅を始めよう。",
        country_italy: "イタリア",
        country_italy_desc: "カラフルなグルメテーマと新しい目標を持ってイタリアを進もう。",
        country_france: "フランス",
        country_france_desc: "独特の食文化と新しいマッチ3の挑戦が待つフランスのチャプターを探索しよう。",
        country_spain: "スペイン",
        country_spain_desc: "スペインの食と雰囲気にインスパイアされた活気ある地中海のチャプターへ足を踏み入れよう。",
        country_japan: "日本",
        country_japan_desc: "おなじみの料理と新しい挑戦が詰まった洗練された日本のチャプターを発見しよう。",
        country_mexico: "メキシコ",
        country_mexico_desc: "エネルギーとグルメに満ちたカラフルな目的地を体験しよう。",
        country_sudan: "スーダン",
        country_sudan_desc: "最後の15のチャレンジでWorldFoodの旅のチャプター1を完結させよう。",
        levels_15: "15 レベル",
        food_pretzel: "プレッツェル",
        food_pizza: "ピザ",
        food_croissant: "クロワッサン",
        food_paella: "パエリア",
        food_sushi: "寿司",
        food_tacos: "タコス",
        food_ful: "フール・メダメス",
        food_section_label: "名物グルメ",
        food_title_1: "グルメを発見",
        food_title_2: "世界中の料理。",
        food_section_desc: "すべての目的地が独自の文化的アイデンティティをWorldFoodアドベンチャーにもたらします。",
        fc_germany: "🇩🇪 ドイツ",
        fc_italy: "🇮🇹 イタリア",
        fc_france: "🇫🇷 フランス",
        fc_spain: "🇪🇸 スペイン",
        fc_japan: "🇯🇵 日本",
        fc_mexico: "🇲🇽 メキシコ",
        fc_sudan: "🇸🇩 スーダン",
        food_pretzel_desc: "ドイツの食文化でおなじみの焼き菓子クラシック。",
        food_pizza_desc: "イタリアの最も有名でカラフルな料理のひとつ。",
        food_croissant_desc: "独特の形をした象徴的なフランスのお気に入り。",
        food_paella_desc: "地中海料理にインスパイアされたカラフルなスペイン料理。",
        food_sushi_desc: "日本料理にインスパイアされたクリーンな形状と鮮やかな色彩。",
        food_tacos_desc: "ワールドツアーに活気をもたらすカラフルな人気メニュー。",
        food_ful_desc: "チャプター1の食の旅を締めくくる伝統的な料理。",
        gp_section_label: "マッチ3ゲームプレイ",
        gp_title_1: "一挙手一投足が",
        gp_title_2: "ボードを変える。",
        gp_section_desc: "限られた手数の中で目標を達成し、フードタイルを組み合わせて強力な特殊ピースを作り出そう。",
        mech_1_title: "3つマッチ",
        mech_1_desc: "一致するタイルをつなげてボードをクリアしよう。",
        mech_2_title: "ライン消去",
        mech_2_desc: "4つのタイルのマッチで、行や列が消去されます。",
        mech_3_title: "ボム",
        mech_3_desc: "T字やL字のマッチで爆発的な特殊タイルを生成します。",
        mech_4_title: "カラーボム",
        mech_4_desc: "5つのタイルのマッチで強力なカラーボムを作り出します。",
        booster_hammer: "ハンマー",
        booster_shuffle: "シャッフル",
        booster_moves: "追加手数",
        feat_section_label: "ゲームの特徴",
        feat_title_1: "プレミアムな冒険のために",
        feat_title_2: "構築されました。",
        feat_1_title: "3Dワールドグローブ",
        feat_1_desc: "世界に焦点を当てたアドベンチャーを通じて目的地を探索しよう。",
        feat_2_title: "3,195レベル",
        feat_2_desc: "213カ国、それぞれ15の手作りチャレンジ。",
        feat_3_title: "特殊タイル",
        feat_3_desc: "ライン消去、ボム、強力なカラーボム。",
        feat_4_title: "スター進行",
        feat_4_desc: "星を獲得して新しい目的地をアンロックしよう。",
        feat_5_title: "報酬",
        feat_5_desc: "マイルストーン、進行状況、報酬を追跡しよう。",
        feat_6_title: "パスポート",
        feat_6_desc: "ワールドツアー全体で完了した目的地を記録しよう。",
        feat_7_title: "音楽＆サウンド",
        feat_7_desc: "オーディオとハプティクスが冒険に命を吹き込みます。",
        feat_8_title: "広告なし",
        feat_8_desc: "広告の中断なしでアドベンチャーを楽しもう。",
        tech_section_label: "Android向けに構築",
        tech_title_1: "最新のテクノロジー。",
        tech_title_2: "ネイティブの体験。",
        tech_section_desc: "WorldFood 3D Adventureは、ネイティブAndroidアプリとして構築されています。",
        tech_lang: "言語",
        tech_ui: "ユーザーインターフェース",
        tech_cloud: "クラウドサービス",
        tech_async: "非同期ランタイム",
        about_section_label: "ゲームについて",
        about_title_1: "パズルゲームの",
        about_title_2: "枠を超えて。",
        about_desc_1: "WorldFood 3D Adventureは、世界の探索、国際的なグルメ、進行、マッチ3ゲームプレイを組み合わせています。",
        about_desc_2: "すべての国が、より大きな世界規模の冒険の新たなチャプターとなります。",
        about_github_link: "GitHubリポジトリを探索 →",
        proj_world_food: "WORLD FOOD",
        proj_active: "アクティブプロジェクト",
        proj_subtitle: "プレミアムマッチ3トラベルアドベンチャー",
        proj_platform: "プラットフォーム",
        proj_language: "言語",
        proj_ui_label: "UI",
        proj_world_label: "ワールド",
        rel_title_1: "冒険の",
        rel_title_2: "準備はいいですか？",
        rel_desc: "世界を旅しよう。グルメを発見しよう。パズルを極めよう。",
        rel_version: "バージョン 1.0.0",
        rel_levels_count: "3,195レベル",
        rel_btn_github: "GitHubでプロジェクトを見る",
        footer_tagline: "世界を旅しよう · グルメを発見しよう · パズルを極めよう",
        footer_explore: "探索",
        footer_game: "ゲーム",
        footer_project: "プロジェクト",
        footer_privacy: "プライバシーポリシー",
        footer_rights: "WorldFood 3D Adventure. All rights reserved.",
        footer_built: "Kotlin · Jetpack Compose · Androidで構築"
    },
    ar: {
        meta_title: "WorldFood 3D Adventure | السفر · الطعام · مطابقة 3",
        nav_home: "الرئيسية",
        nav_adventure: "المغامرة",
        nav_world: "العالم",
        nav_food: "الطعام",
        nav_gameplay: "اللعب",
        nav_features: "المميزات",
        nav_about: "عن اللعبة",
        nav_release: "الإصدار",
        cta_play_soon: "العب قريباً",
        hero_badge: "طعام عالمي · مطابقة 3 · مغامرة سفر",
        hero_title_1: "سافر حول العالم.",
        hero_title_2: "اكتشف الطعام.",
        hero_title_3: "أتقن الألغاز.",
        hero_desc: "استكشف 213 وجهة في مغامرة سفر مطابقة 3 فاخرة. اكتشف الأطعمة الشهيرة، واجمع النجوم، وافتح بلداناً جديدة، وأتقن 3,195 مستوى ألغاز مصمماً يدوياً.",
        hero_btn_explore: "استكشف المغامرة",
        hero_btn_gameplay: "عرض اللعب",
        stat_levels: "المستويات",
        stat_countries: "البلدان",
        stat_per_country: "لكل بلد",
        stat_ads: "إعلانات",
        globe_loading: "جاري تحميل العالم",
        globe_drag: "اسحب الكوكب",
        card_world_tour: "جولة عالمية",
        country_germany: "ألمانيا",
        card_level: "المستوى",
        card_level_10: "المستوى 10",
        card_moves: "الحركات",
        card_goal: "الهدف",
        adv_label: "المغامرة",
        adv_title_1: "رحلة واحدة.",
        adv_title_2: "عالم من الاكتشافات.",
        adv_desc: "استكشاف العالم، الطعام الدولي، وأسلوب لعب المطابقة اليدوي يجتمعون في مغامرة واحدة متصلة.",
        adv_card1_title: "استكشف العالم",
        adv_card1_desc: "سافر عبر العالم، واجمع النجوم وافتح وجهات جديدة مع نمو مغامرتك.",
        adv_card1_link: "استكشاف العالم ←",
        adv_card2_title: "اكتشف الطعام",
        adv_card2_desc: "اكتشف طعاماً مستوحى من البلدان والثقافات من حول العالم.",
        adv_card2_link: "اكتشف الأطباق ←",
        adv_card3_title: "أتقن مطابقة 3",
        adv_card3_desc: "أنشئ مربعات خاصة، وفعل تفاعلات متسلسلة وأتقن أهدافاً تزداد تحدياً.",
        adv_card3_link: "أتقن الألغاز ←",
        countries_label: "جولة عالمية",
        countries_title_1: "213 وجهة.",
        countries_title_2: "مغامرة واحدة.",
        countries_desc: "ابدأ من أوروبا واستر في جميع أنحاء العالم بينما تفتح فصولاً جديدة من خلال التقدم.",
        ch_1: "الفصل 01",
        ch_2: "الفصل 02",
        ch_3: "الفصل 03",
        ch_4: "الفصل 04",
        ch_5: "الفصل 05",
        ch_6: "الفصل 06",
        ch_7: "الفصل 07 · الختام",
        country_germany_desc: "ابدأ الرحلة مع أول خمسة عشر تحدياً يدوياً في ألمانيا.",
        country_italy: "إيطاليا",
        country_italy_desc: "تابع عبر إيطاليا مع مواضيع طعام ملونة وأهداف جديدة.",
        country_france: "فرنسا",
        country_france_desc: "استكشف فصلاً فرنسانياً بطابع مطبخ مميز وتحديات مطابقة جديدة.",
        country_spain: "إسبانيا",
        country_spain_desc: "ادخل فصلاً متوسطياً حيوياً مستوحى من الطعام والأجواء الإسبانية.",
        country_japan: "اليابان",
        country_japan_desc: "اكتشف فصلاً يابانياً راقياً بمأكولات معروفة وتحديات جديدة.",
        country_mexico: "المكسيك",
        country_mexico_desc: "عش تجربة وجهة ملونة مليئة بالطاقة والألغاز المستوحاة من الطعام.",
        country_sudan: "السودان",
        country_sudan_desc: "أكمل الفصل 1 من رحلة WorldFood مع التحديات الخمسة عشر الأخيرة.",
        levels_15: "15 مستوى",
        food_pretzel: "برييتزل",
        food_pizza: "بيتزا",
        food_croissant: "كرواسون",
        food_paella: "باييلا",
        food_sushi: "سوشي",
        food_tacos: "تاكو",
        food_ful: "فول مدمس",
        food_section_label: "الأطعمة المميزة",
        food_title_1: "اكتشف الطعام",
        food_title_2: "حول العالم.",
        food_section_desc: "تجلب كل وجهة هويتها الطهوية الخاصة إلى مغامرة WorldFood.",
        fc_germany: "🇩🇪 ألمانيا",
        fc_italy: "🇮🇹 إيطاليا",
        fc_france: "🇫🇷 فرنسا",
        fc_spain: "🇪🇸 إسبانيا",
        fc_japan: "🇯🇵 اليابان",
        fc_mexico: "🇲🇽 المكسيك",
        fc_sudan: "🇸🇩 السودان",
        food_pretzel_desc: "معجنات مخبوزة شهيرة من ثقافة الطعام الألمانية.",
        food_pizza_desc: "واحدة من أشهر وألوان أطباق إيطاليا.",
        food_croissant_desc: "المفضل الفرنسي الأيقوني بشكله المميز.",
        food_paella_desc: "طبق إسباني ملون مستوحى من مطبخ البحر الأبيض المتوسط.",
        food_sushi_desc: "أشكال نقية وألوان زاهية مستوحاة من المطبخ الياباني.",
        food_tacos_desc: "مفضل ملون يجلب الطاقة إلى الجولة العالمية.",
        food_ful_desc: "طبق تقليدي يكمل الرحلة الطهوية للفصل الأول.",
        gp_section_label: "لعبة مطابقة 3",
        gp_title_1: "كل حركة يمكن أن",
        gp_title_2: "تغير اللوحة.",
        gp_section_desc: "أكمل الأهداف ضمن حركات محدودة، واجمع مربعات الطعام وأنشئ قطعاً خاصة قوية.",
        mech_1_title: "مطابقة ثلاثة",
        mech_1_desc: "اربط المربعات المتطابقة لتفريغ اللوحة.",
        mech_2_title: "مسح الصفوف",
        mech_2_desc: "تطابقات الأربع مربعات تنظف صفوفاً أو أعمدة كاملة.",
        mech_3_title: "قنابل",
        mech_3_desc: "تطابقات حرفي T و L تخلق مربعات خاصة متفجرة.",
        mech_4_title: "قنبلة الألوان",
        mech_4_desc: "تطابقات الخمس مربعات تخلق قنابل ألوان قوية.",
        booster_hammer: "مطرقة",
        booster_shuffle: "خلط",
        booster_moves: "حركات",
        feat_section_label: "مميزات اللعبة",
        feat_title_1: "مصممة من أجل",
        feat_title_2: "مغامرة فاخرة.",
        feat_1_title: "كوكب الأرض ثلاثي الأبعاد",
        feat_1_desc: "استكشف الوجهات من خلال مغامرة تركز على العالم.",
        feat_2_title: "3,195 مستوى",
        feat_2_desc: "213 بلداً مع خمسة عشر تحدياً مصمماً يدوياً لكل منها.",
        feat_3_title: "مربعات خاصة",
        feat_3_desc: "مسح الخطوط والقنابل وقنابل الألوان القوية.",
        feat_4_title: "تقدم النجوم",
        feat_4_desc: "اربح النجوم وافتح وجهات جديدة.",
        feat_5_title: "المكافآت",
        feat_5_desc: "تتبع الإنجازات والتقدم والمكافآت.",
        feat_6_title: "جواز السفر",
        feat_6_desc: "سجل الوجهات المكتملة طوال الجولة العالمية.",
        feat_7_title: "الموسيقى والصوت",
        feat_7_desc: "الصوت والتأثيرات اللمسية تبث الحياة في المغامرة.",
        feat_8_title: "بدون إعلانات",
        feat_8_desc: "مغامرة بدون انقطاعات إعلانية.",
        tech_section_label: "مصممة لنظام أندرويد",
        tech_title_1: "تقنية حديثة.",
        tech_title_2: "تجربة أصلية.",
        tech_section_desc: "تم بناء WorldFood 3D Adventure كaplikasi أندرويد أصلية.",
        tech_lang: "اللغة",
        tech_ui: "واجهة المستخدم",
        tech_cloud: "خدمات السحابة",
        tech_async: "بيئة التشغيل غير المتزامنة",
        about_section_label: "عن اللعبة",
        about_title_1: "أكثر من مجرد",
        about_title_2: "لعبة ألغاز.",
        about_desc_1: "تجمع WorldFood 3D Adventure بين استكشاف العالم، الطعام الدولي، التقدم وأسلوب مطابقة 3.",
        about_desc_2: "يصبح كل بلد فصلاً آخر في مغامرة عالمية أكبر.",
        about_github_link: "استكشف مستودع GitHub ←",
        proj_world_food: "WORLD FOOD",
        proj_active: "مشروع نشط",
        proj_subtitle: "مغامرة سفر مطابقة 3 فاخرة",
        proj_platform: "المنصة",
        proj_language: "اللغة",
        proj_ui_label: "الواجهة",
        proj_world_label: "العالم",
        rel_title_1: "هل أنت مستعد",
        rel_title_2: "للمغامرة؟",
        rel_desc: "سافر حول العالم. اكتشف الطعام. أتقن الألغاز.",
        rel_version: "الإصدار 1.0.0",
        rel_levels_count: "3,195 مستوى",
        rel_btn_github: "عرض المشروع على GitHub",
        footer_tagline: "سافر حول العالم · اكتشف الطعام · أتقن الألغاز",
        footer_explore: "استكشف",
        footer_game: "اللعبة",
        footer_project: "المشروع",
        footer_privacy: "سياسة الخصوصية",
        footer_rights: "WorldFood 3D Adventure. جميع الحقوق محفوظة.",
        footer_built: "بُنيت باستخدام Kotlin · Jetpack Compose · Android"
    },
    ru: {
        meta_title: "WorldFood 3D Adventure | Путешествия · Еда · Три в ряд",
        nav_home: "Главная",
        nav_adventure: "Приключение",
        nav_world: "Мир",
        nav_food: "Еда",
        nav_gameplay: "Геймплей",
        nav_features: "Особенности",
        nav_about: "О игре",
        nav_release: "Релиз",
        cta_play_soon: "Скоро игра",
        hero_badge: "МИРОВАЯ ЕДА · ТРИ В РЯД · ПРИКЛЮЧЕНИЕ",
        hero_title_1: "Путешествуйте по миру.",
        hero_title_2: "Откройте для себя еду.",
        hero_title_3: "Освойте головоломку.",
        hero_desc: "Исследуйте 213 мест в премиальном приключении «Три в ряд». Открывайте культовые блюда, собирайте звезды, открывайте новые страны и пройдите 3 195 созданных вручную уровней.",
        hero_btn_explore: "Исследовать приключение",
        hero_btn_gameplay: "Смотреть геймплей",
        stat_levels: "УРОВНЕЙ",
        stat_countries: "СТРАН",
        stat_per_country: "В СТРАНЕ",
        stat_ads: "РЕКЛАМЫ",
        globe_loading: "ЗАГРУЗКА МИРА",
        globe_drag: "ВРАЩАЙТЕ ЗЕМЛЮ",
        card_world_tour: "МИРОВОЕ ТУРНЕ",
        country_germany: "ГЕРМАНИЯ",
        card_level: "УРОВЕНЬ",
        card_level_10: "УРОВЕНЬ 10",
        card_moves: "ХОДЫ",
        card_goal: "ЦЕЛЬ",
        adv_label: "ПРИКЛЮЧЕНИЕ",
        adv_title_1: "Одно путешествие.",
        adv_title_2: "Мир открытий.",
        adv_desc: "Исследование мира, международная кухня и ручной геймплей «Три в ряд» объединены в одном приключении.",
        adv_card1_title: "Исследуйте мир",
        adv_card1_desc: "Путешествуйте по миру, собирайте звезды и открывайте новые направления по мере прохождения.",
        adv_card1_link: "Исследование мира →",
        adv_card2_title: "Откройте еду",
        adv_card2_desc: "Откройте для себя блюда, вдохновленные странами и культурами со всего мира.",
        adv_card2_link: "Изучить блюда →",
        adv_card3_title: "Освойте «Три в ряд»",
        adv_card3_desc: "Создавайте особые плитки, запускайте цепные реакции и решайте сложные задачи.",
        adv_card3_link: "Освоить головоломки →",
        countries_label: "МИРОВОЕ ТУРНЕ",
        countries_title_1: "213 направлений.",
        countries_title_2: "Одно приключение.",
        countries_desc: "Начните в Европе и продолжайте путешествие по всему миру, открывая новые главы по мере прогресса.",
        ch_1: "ГЛАВА 01",
        ch_2: "ГЛАВА 02",
        ch_3: "ГЛАВА 03",
        ch_4: "ГЛАВА 04",
        ch_5: "ГЛАВА 05",
        ch_6: "ГЛАВА 06",
        ch_7: "ГЛАВА 07 · ФИНАЛ",
        country_germany_desc: "Начните путешествие с первых пятнадцати уникальных испытаний в Германии.",
        country_italy: "Италия",
        country_italy_desc: "Продолжайте путешествие по Италии с яркими кулинарными темами и новыми целями.",
        country_france: "Франция",
        country_france_desc: "Исследуйте французскую главу с уникальной кухней и новыми испытаниями.",
        country_spain: "Испания",
        country_spain_desc: "Погрузитесь в яркую средиземноморскую главу, вдохновленную испанской кухней и атмосферой.",
        country_japan: "Япония",
        country_japan_desc: "Откройте утонченную японскую главу с узнаваемой кухней и новыми испытаниями.",
        country_mexico: "Мексика",
        country_mexico_desc: "Ощутите яркое направление, полное энергии и головоломок на тему еды.",
        country_sudan: "Судан",
        country_sudan_desc: "Завершите главу 1 путешествия WorldFood последними пятнадцатью испытаниями.",
        levels_15: "15 уровней",
        food_pretzel: "Крендель",
        food_pizza: "Пицца",
        food_croissant: "Круассан",
        food_paella: "Паэлья",
        food_sushi: "Суши",
        food_tacos: "Тако",
        food_ful: "Фул медамес",
        food_section_label: "ФИРМЕННЫЕ БЛЮДА",
        food_title_1: "Откройте еду",
        food_title_2: "по всему миру.",
        food_section_desc: "Каждое направление привносит свою кулинарную индивидуальность в приключение WorldFood.",
        fc_germany: "🇩🇪 ГЕРМАНИЯ",
        fc_italy: "🇮🇹 ИТАЛИЯ",
        fc_france: "🇫🇷 ФРАНЦИЯ",
        fc_spain: "🇪🇸 ИСПАНИЯ",
        fc_japan: "🇯🇵 ЯПОНИЯ",
        fc_mexico: "🇲🇽 МЕКСИКА",
        fc_sudan: "🇸🇩 СУДАН",
        food_pretzel_desc: "Узнаваемая выпечка из немецкой культуры питания.",
        food_pizza_desc: "Одно из самых известных и красочных блюд Италии.",
        food_croissant_desc: "Культовый французский фаворит с неповторимой формой.",
        food_paella_desc: "Красочное испанское блюдо, вдохновленное средиземноморской кухней.",
        food_sushi_desc: "Чистые формы и яркие цвета, вдохновленные японской кухней.",
        food_tacos_desc: "Яркое любимое блюдо, привносящее энергию в мировое турне.",
        food_ful_desc: "Традиционное блюдо, завершающее кулинарное путешествие Главы 1.",
        gp_section_label: "ГЕЙМПЛЕЙ ТРИ В РЯД",
        gp_title_1: "Каждый ход может",
        gp_title_2: "изменить доску.",
        gp_section_desc: "Выполняйте цели за ограниченное число ходов, комбинируйте плитки еды и создавайте мощные бонусы.",
        mech_1_title: "Три в ряд",
        mech_1_desc: "Соединяйте одинаковые плитки, чтобы очистить поле.",
        mech_2_title: "Очистка линий",
        mech_2_desc: "Совпадение четырех плиток очищает ряды или столбцы.",
        mech_3_title: "Бомбы",
        mech_3_desc: "Совпадения в виде T и L создают взрывные особые плитки.",
        mech_4_title: "Цветная бомба",
        mech_4_desc: "Совпадение пяти плиток создает мощную цветную бомбу.",
        booster_hammer: "Молоток",
        booster_shuffle: "Перемешать",
        booster_moves: "Ходы",
        feat_section_label: "ОСОБЕННОСТИ ИГРЫ",
        feat_title_1: "Создано для",
        feat_title_2: "премиального приключения.",
        feat_1_title: "3D модель Земли",
        feat_1_desc: "Исследуйте направления в путешествии по всему миру.",
        feat_2_title: "3 195 уровней",
        feat_2_desc: "213 стран, по пятнадцать уникальных испытаний в каждой.",
        feat_3_title: "Особые плитки",
        feat_3_desc: "Очистка линий, бомбы и мощные цветные бомбы.",
        feat_4_title: "Прогресс звезд",
        feat_4_desc: "Зарабатывайте звезды и открывайте новые направления.",
        feat_5_title: "Награды",
        feat_5_desc: "Отслеживайте вехи, прогресс и награды.",
        feat_6_title: "Паспорт",
        feat_6_desc: "Фиксируйте пройденные направления во время мирового турне.",
        feat_7_title: "Музыка и звук",
        feat_7_desc: "Аудио и тактильная отдача оживляют приключение.",
        feat_8_title: "Без рекламы",
        feat_8_desc: "Приключения без рекламных пауз.",
        tech_section_label: "СОЗДАНО ДЛЯ ANDROID",
        tech_title_1: "Современные технологии.",
        tech_title_2: "Нативный опыт.",
        tech_section_desc: "WorldFood 3D Adventure создана как нативное Android-приложение.",
        tech_lang: "Язык",
        tech_ui: "Интерфейс",
        tech_cloud: "Облачные службы",
        tech_async: "Асинхронная среда",
        about_section_label: "ОБ ИГРЕ",
        about_title_1: "Больше, чем",
        about_title_2: "просто игра-головоломка.",
        about_desc_1: "WorldFood 3D Adventure сочетает исследование мира, международную еду, прогресс и геймплей «Три в ряд».",
        about_desc_2: "Каждая страна становится еще одной главой в масштабном глобальном приключении.",
        about_github_link: "Исследовать репозиторий GitHub →",
        proj_world_food: "WORLD FOOD",
        proj_active: "АКТИВНЫЙ ПРОЕКТ",
        proj_subtitle: "Премиальное приключение «Три в ряд»",
        proj_platform: "ПЛАТФОРМА",
        proj_language: "ЯЗЫК",
        proj_ui_label: "UI",
        proj_world_label: "МИР",
        rel_title_1: "Готовы к",
        rel_title_2: "приключению?",
        rel_desc: "Путешествуйте по миру. Откройте для себя еду. Освойте головоломку.",
        rel_version: "Версия 1.0.0",
        rel_levels_count: "3 195 уровней",
        rel_btn_github: "Посмотреть проект на GitHub",
        footer_tagline: "Путешествуйте по миру · Откройте еду · Освойте головоломку",
        footer_explore: "ИССЛЕДОВАТЬ",
        footer_game: "ИГРА",
        footer_project: "ПРОЕКТ",
        footer_privacy: "Политика конфиденциальности",
        footer_rights: "WorldFood 3D Adventure. Все права защищены.",
        footer_built: "Создано с помощью Kotlin · Jetpack Compose · Android"
    }
};

document.addEventListener(
    "DOMContentLoaded",
    () => {

        /* =====================================================
           MULTILINGUAL SYSTEM (i18n) LOGIC
        ===================================================== */
        const languageSelect = document.getElementById("language-select");

        const setLanguage = (lang) => {
            if (!translations[lang]) lang = "en";
            
            // Text Translation
            document.querySelectorAll("[data-i18n]").forEach(el => {
                const key = el.getAttribute("data-i18n");
                if (translations[lang][key]) {
                    el.textContent = translations[lang][key];
                }
            });

            // Meta Title Translation
            const metaTitleEl = document.querySelector('title[data-i18n="meta_title"]');
            if (metaTitleEl && translations[lang]["meta_title"]) {
                document.title = translations[lang]["meta_title"];
            }

            // HTML attribute updates for RTL support (Arabic)
            document.documentElement.setAttribute("lang", lang);
            document.documentElement.setAttribute("data-lang", lang);
            if (lang === "ar") {
                document.documentElement.setAttribute("dir", "rtl");
            } else {
                document.documentElement.setAttribute("dir", "ltr");
            }

            if (languageSelect) {
                languageSelect.value = lang;
            }

            localStorage.setItem("preferred_language", lang);
        };

        // Initialize Language based on storage or browser
        const savedLang = localStorage.getItem("preferred_language");
        const browserLang = navigator.language ? navigator.language.slice(0, 2) : "en";
        const initialLang = savedLang || (translations[browserLang] ? browserLang : "en");

        setLanguage(initialLang);

        if (languageSelect) {
            languageSelect.addEventListener("change", (e) => {
                setLanguage(e.target.value);
            });
        }


        /* =====================================================
           WEBSITE ELEMENTS
        ===================================================== */

        const header =
            document.querySelector(
                "[data-header]"
            );

        const menuButton =
            document.querySelector(
                "[data-menu-button]"
            );

        const mobileMenu =
            document.querySelector(
                "[data-mobile-menu]"
            );

        const backToTop =
            document.querySelector(
                "[data-back-top]"
            );

        const year =
            document.querySelector(
                "[data-current-year]"
            );

        const revealElements =
            document.querySelectorAll(
                "[data-reveal]"
            );

        const internalLinks =
            document.querySelectorAll(
                'a[href^="#"]'
            );


        /* =====================================================
           CURRENT YEAR
        ===================================================== */

        if (year) {

            year.textContent =
                new Date()
                    .getFullYear();

        }


        /* =====================================================
           MOBILE MENU
        ===================================================== */

        const closeMenu =
            () => {

                if (
                    !menuButton ||
                    !mobileMenu
                ) {
                    return;
                }

                mobileMenu
                    .classList
                    .remove(
                        "is-open"
                    );

                menuButton
                    .setAttribute(
                        "aria-expanded",
                        "false"
                    );

                document.body
                    .classList
                    .remove(
                        "menu-open"
                    );
            };


        if (
            menuButton &&
            mobileMenu
        ) {

            menuButton
                .addEventListener(
                    "click",
                    () => {

                        const open =
                            mobileMenu
                                .classList
                                .toggle(
                                    "is-open"
                                );

                        menuButton
                            .setAttribute(
                                "aria-expanded",
                                String(open)
                            );

                        document.body
                            .classList
                            .toggle(
                                "menu-open",
                                open
                            );
                    }
                );

        }


        document.addEventListener(
            "keydown",
            event => {

                if (
                    event.key ===
                    "Escape"
                ) {
                    closeMenu();
                }
            }
        );


        /* =====================================================
           SMOOTH SCROLL
        ===================================================== */

        internalLinks.forEach(
            link => {

                link.addEventListener(
                    "click",
                    event => {

                        const href =
                            link.getAttribute(
                                "href"
                            );

                        if (
                            !href ||
                            href === "#"
                        ) {
                            return;
                        }

                        let target;

                        try {

                            target =
                                document
                                    .querySelector(
                                        href
                                    );

                        } catch {

                            return;

                        }


                        if (!target) {
                            return;
                        }


                        event.preventDefault();


                        const headerHeight =
                            header
                                ? header.offsetHeight
                                : 0;


                        const targetY =
                            target
                                .getBoundingClientRect()
                                .top
                            +
                            window.scrollY
                            -
                            headerHeight
                            -
                            10;


                        window.scrollTo({
                            top:
                                targetY,

                            behavior:
                                "smooth"
                        });


                        closeMenu();
                    }
                );

            }
        );


        /* =====================================================
           HEADER
        ===================================================== */

        const updateHeader =
            () => {

                if (!header) {
                    return;
                }

                header.classList.toggle(
                    "is-scrolled",
                    window.scrollY > 24
                );
            };


        updateHeader();


        window.addEventListener(
            "scroll",
            updateHeader,
            {
                passive: true
            }
        );


        /* =====================================================
           BACK TO TOP
        ===================================================== */

        if (backToTop) {

            const updateBackToTop =
                () => {

                    backToTop
                        .classList
                        .toggle(
                            "visible",
                            window.scrollY >
                            650
                        );
                };


            updateBackToTop();


            window.addEventListener(
                "scroll",
                updateBackToTop,
                {
                    passive: true
                }
            );


            backToTop
                .addEventListener(
                    "click",
                    () => {

                        window.scrollTo({
                            top: 0,
                            behavior:
                                "smooth"
                        });
                    }
                );
        }


        /* =====================================================
           REVEAL SECTIONS
        ===================================================== */

        const reducedMotion =
            window.matchMedia(
                "(prefers-reduced-motion: reduce)"
            ).matches;


        if (
            reducedMotion ||
            !(
                "IntersectionObserver"
                in window
            )
        ) {

            revealElements
                .forEach(
                    element => {

                        element
                            .classList
                            .add(
                                "is-visible"
                            );
                    }
                );

        } else {

            const revealObserver =
                new IntersectionObserver(
                    (
                        entries,
                        observer
                    ) => {

                        entries
                            .forEach(
                                entry => {

                                    if (
                                        !entry
                                            .isIntersecting
                                    ) {
                                        return;
                                    }

                                    entry
                                        .target
                                        .classList
                                        .add(
                                            "is-visible"
                                        );

                                    observer
                                        .unobserve(
                                            entry.target
                                        );
                                }
                            );
                    },
                    {
                        threshold:
                            0.12,

                        rootMargin:
                            "0px 0px -45px 0px"
                    }
                );


            revealElements
                .forEach(
                    element => {

                        revealObserver
                            .observe(
                                element
                            );
                    }
                );
        }


        /* =====================================================
           ACTIVE NAVIGATION
        ===================================================== */

        const sections =
            document.querySelectorAll(
                "main section[id]"
            );

        const desktopLinks =
            document.querySelectorAll(
                '.desktop-nav a[href^="#"]'
            );


        if (
            sections.length &&
            "IntersectionObserver"
            in window
        ) {

            const sectionObserver =
                new IntersectionObserver(
                    entries => {

                        entries.forEach(
                            entry => {

                                if (
                                    !entry
                                        .isIntersecting
                                ) {
                                    return;
                                }

                                const id =
                                    entry.target.id;


                                desktopLinks
                                    .forEach(
                                        link => {

                                            link.classList
                                                .toggle(
                                                    "is-active",

                                                    link
                                                        .getAttribute(
                                                            "href"
                                                        )
                                                    ===
                                                    `#${id}`
                                                );
                                        }
                                    );
                            }
                        );
                    },
                    {
                        rootMargin:
                            "-35% 0px -55% 0px"
                    }
                );


            sections.forEach(
                section => {

                    sectionObserver
                        .observe(
                            section
                        );
                }
            );

        }


        /* =====================================================
           RESIZE MENU RESET
        ===================================================== */

        window.addEventListener(
            "resize",
            () => {

                if (
                    window.innerWidth >
                    900
                ) {
                    closeMenu();
                }
            }
        );


        /* =====================================================
           3D WORLD GLOBE
        ===================================================== */

        const canvas =
            document.getElementById(
                "world-globe-canvas"
            );

        const loading =
            document.getElementById(
                "globe-loading"
            );


        if (!canvas) {
            return;
        }


        /* =====================================================
           THREE SCENE
        ===================================================== */

        const scene =
            new THREE.Scene();


        const camera =
            new THREE
                .PerspectiveCamera(
                    35,
                    1,
                    0.1,
                    100
                );


        camera.position.set(
            0,
            0,
            3.25
        );


        /* =====================================================
           RENDERER
        ===================================================== */

        let renderer;


        try {

            renderer =
                new THREE.WebGLRenderer({
                    canvas:
                        canvas,

                    alpha:
                        true,

                    antialias:
                        true,

                    powerPreference:
                        "high-performance"
                });

        } catch (error) {

            console.error(
                "3D globe could not start:",
                error
            );

            if (loading) {
                loading.innerHTML =
                    "3D globe unavailable";
            }

            return;
        }


        renderer.setPixelRatio(
            Math.min(
                window.devicePixelRatio ||
                1,
                2
            )
        );


        renderer.setClearColor(
            0x000000,
            0
        );


        renderer.outputColorSpace =
            THREE.SRGBColorSpace;


        /* =====================================================
           EARTH GROUP
        ===================================================== */

        const earthGroup =
            new THREE.Group();


        earthGroup.rotation.x =
            THREE.MathUtils
                .degToRad(
                    -8
                );


        earthGroup.rotation.z =
            THREE.MathUtils
                .degToRad(
                    -23.4
                );


        scene.add(
            earthGroup
        );


        /* =====================================================
           EARTH GEOMETRY
        ===================================================== */

        const earthGeometry =
            new THREE
                .SphereGeometry(
                    1,
                    96,
                    96
                );


        /* =====================================================
           TEXTURE
        ===================================================== */

        const textureLoader =
            new THREE.TextureLoader();


        const textureUrl =
            "https://threejs.org/examples/textures/planets/earth_atmos_2048.jpg";


        textureLoader.load(
            textureUrl,

            texture => {

                texture.colorSpace =
                    THREE.SRGBColorSpace;


                texture.anisotropy =
                    Math.min(
                        8,
                        renderer
                            .capabilities
                            .getMaxAnisotropy()
                    );


                /* =================================================
                   EARTH MATERIAL
                ================================================= */

                const earthMaterial =
                    new THREE
                        .MeshPhongMaterial({
                            map:
                                texture,

                            color:
                                0xffffff,

                            shininess:
                                10,

                            specular:
                                new THREE.Color(
                                    0x336c8f
                                )
                        });


                const earth =
                    new THREE.Mesh(
                        earthGeometry,
                        earthMaterial
                    );


                earthGroup.add(
                    earth
                );


                /* =================================================
                   CLOUD-LIKE SOFT SHELL
                ================================================= */

                const atmosphereGeometry =
                    new THREE
                        .SphereGeometry(
                            1.025,
                            96,
                            96
                        );


                const atmosphereMaterial =
                    new THREE
                        .MeshPhongMaterial({
                            color:
                                0x64b7ff,

                            transparent:
                                true,

                            opacity:
                                0.055,

                            side:
                                THREE.FrontSide,

                            depthWrite:
                                false
                        });


                const atmosphere =
                    new THREE.Mesh(
                        atmosphereGeometry,
                        atmosphereMaterial
                    );


                earthGroup.add(
                    atmosphere
                );


                /* =================================================
                   ATMOSPHERE GLOW
                ================================================= */

                const glowGeometry =
                    new THREE
                        .SphereGeometry(
                            1.075,
                            64,
                            64
                        );


                const glowMaterial =
                    new THREE
                        .ShaderMaterial({
                            uniforms: {
                                glowColor: {
                                    value:
                                        new THREE
                                            .Color(
                                                0x168cff
                                            )
                                }
                            },

                            vertexShader: `
                                varying vec3 vNormal;

                                void main() {
                                    vNormal =
                                        normalize(
                                            normalMatrix *
                                            normal
                                        );

                                    gl_Position =
                                        projectionMatrix *
                                        modelViewMatrix *
                                        vec4(
                                            position,
                                            1.0
                                        );
                                }
                            `,

                            fragmentShader: `
                                varying vec3 vNormal;
                                uniform vec3 glowColor;

                                void main() {

                                    float intensity =
                                        pow(
                                            0.72 -
                                            dot(
                                                vNormal,
                                                vec3(
                                                    0.0,
                                                    0.0,
                                                    1.0
                                                )
                                            ),
                                            2.1
                                        );

                                    gl_FragColor =
                                        vec4(
                                            glowColor,
                                            intensity *
                                            0.45
                                        );
                                }
                            `,

                            side:
                                THREE.BackSide,

                            blending:
                                THREE.AdditiveBlending,

                            transparent:
                                true,

                            depthWrite:
                                false
                        });


                const glow =
                    new THREE.Mesh(
                        glowGeometry,
                        glowMaterial
                    );


                earthGroup.add(
                    glow
                );


                if (loading) {

                    loading
                        .classList
                        .add(
                            "is-hidden"
                        );
                }

            },

            undefined,

            error => {

                console.error(
                    "Earth texture error:",
                    error
                );

                const fallbackMaterial =
                    new THREE
                        .MeshPhongMaterial({
                            color:
                                0x096bb5,

                            shininess:
                                18
                        });


                const fallbackEarth =
                    new THREE.Mesh(
                        earthGeometry,
                        fallbackMaterial
                    );


                earthGroup.add(
                    fallbackEarth
                );


                if (loading) {

                    loading
                        .classList
                        .add(
                            "is-hidden"
                        );
                }
            }
        );


        /* =====================================================
           LIGHTING
        ===================================================== */

        const ambientLight =
            new THREE
                .AmbientLight(
                    0x769fc3,
                    1.15
                );


        scene.add(
            ambientLight
        );


        const sunlight =
            new THREE
                .DirectionalLight(
                    0xffffff,
                    3.15
                );


        sunlight.position.set(
            -3.5,
            2.7,
            4.5
        );


        scene.add(
            sunlight
        );


        const blueRimLight =
            new THREE
                .DirectionalLight(
                    0x167dff,
                    1.35
                );


        blueRimLight
            .position
            .set(
                4,
                -1,
                -3
            );


        scene.add(
            blueRimLight
        );


        /* =====================================================
           STAR FIELD
        ===================================================== */

        const starsCount =
            window.innerWidth < 620
                ? 160
                : 340;


        const starPositions =
            new Float32Array(
                starsCount *
                3
            );


        for (
            let i = 0;
            i < starsCount;
            i++
        ) {

            const offset =
                i * 3;


            starPositions[offset] =
                (
                    Math.random() -
                    .5
                ) * 11;


            starPositions[offset + 1] =
                (
                    Math.random() -
                    .5
                ) * 11;


            starPositions[offset + 2] =
                (
                    Math.random() -
                    .5
                ) * 6;
        }


        const starsGeometry =
            new THREE
                .BufferGeometry();


        starsGeometry
            .setAttribute(
                "position",

                new THREE
                    .BufferAttribute(
                        starPositions,
                        3
                    )
            );


        const starsMaterial =
            new THREE
                .PointsMaterial({
                    color:
                        0xffffff,

                    size:
                        .012,

                    opacity:
                        .32,

                    transparent:
                        true,

                    depthWrite:
                        false
                });


        const starField =
            new THREE.Points(
                starsGeometry,
                starsMaterial
            );


        scene.add(
            starField
        );


        /* =====================================================
           RESPONSIVE CANVAS
        ===================================================== */

        const resizeRenderer =
            () => {

                const width =
                    canvas
                        .clientWidth;

                const height =
                    canvas
                        .clientHeight;


                if (
                    width === 0 ||
                    height === 0
                ) {
                    return;
                }


                renderer.setSize(
                    width,
                    height,
                    false
                );


                camera.aspect =
                    width /
                    height;


                camera
                    .updateProjectionMatrix();
            };


        resizeRenderer();


        window.addEventListener(
            "resize",
            resizeRenderer,
            {
                passive: true
            }
        );


        /* =====================================================
           INTERACTIVE ROTATION
        ===================================================== */

        let dragging =
            false;


        let lastPointerX =
            0;


        let lastPointerY =
            0;


        let rotationVelocityX =
            0;


        let rotationVelocityY =
            0;


        canvas.addEventListener(
            "pointerdown",
            event => {

                dragging =
                    true;


                lastPointerX =
                    event.clientX;


                lastPointerY =
                    event.clientY;


                canvas
                    .setPointerCapture(
                        event.pointerId
                    );
            }
        );


        canvas.addEventListener(
            "pointermove",
            event => {

                if (!dragging) {
                    return;
                }


                const dx =
                    event.clientX -
                    lastPointerX;


                const dy =
                    event.clientY -
                    lastPointerY;


                rotationVelocityX =
                    dx *
                    .004;


                rotationVelocityY =
                    dy *
                    .003;


                earthGroup
                    .rotation
                    .y +=
                    rotationVelocityX;


                earthGroup
                    .rotation
                    .x +=
                    rotationVelocityY;


                earthGroup.rotation.x =
                    Math.max(
                        -.7,

                        Math.min(
                            .7,
                            earthGroup
                                .rotation
                                .x
                        )
                    );


                lastPointerX =
                    event.clientX;


                lastPointerY =
                    event.clientY;
            }
        );


        const finishPointer =
            event => {

                dragging =
                    false;


                if (
                    event &&
                    canvas.hasPointerCapture(
                        event.pointerId
                    )
                ) {

                    canvas
                        .releasePointerCapture(
                            event.pointerId
                        );
                }
            };


        canvas.addEventListener(
            "pointerup",
            finishPointer
        );


        canvas.addEventListener(
            "pointercancel",
            finishPointer
        );


        /* =====================================================
           ANIMATION
        ===================================================== */

        let previousFrame =
            performance.now();


        let frameId =
            null;


        let pageVisible =
            !document.hidden;


        const animate =
            time => {

                if (!pageVisible) {

                    frameId =
                        requestAnimationFrame(
                            animate
                        );

                    previousFrame =
                        time;

                    return;
                }


                const deltaTime =
                    Math.min(
                        (
                            time -
                            previousFrame
                        ) /
                        1000,
                        .05
                    );


                previousFrame =
                    time;


                if (!dragging) {

                    earthGroup.rotation.y +=
                        0.085 *
                        deltaTime;

                    earthGroup.rotation.y +=
                        rotationVelocityX;

                    earthGroup.rotation.x +=
                        rotationVelocityY;

                    rotationVelocityX *=
                        0.92;

                    rotationVelocityY *=
                        0.92;
                }


                if (!reducedMotion) {

                    starField
                        .rotation
                        .y +=
                        .008 *
                        deltaTime;

                }


                renderer.render(
                    scene,
                    camera
                );


                frameId =
                    requestAnimationFrame(
                        animate
                    );
            };


        frameId =
            requestAnimationFrame(
                animate
            );


        /* =====================================================
           PAUSE IF TAB IS HIDDEN
        ===================================================== */

        document.addEventListener(
            "visibilitychange",
            () => {

                pageVisible =
                    !document.hidden;
            }
        );


        /* =====================================================
           CLEAN UP
        ===================================================== */

        window.addEventListener(
            "beforeunload",
            () => {

                if (frameId) {

                    cancelAnimationFrame(
                        frameId
                    );
                }


                earthGeometry.dispose();

                starsGeometry.dispose();

                starsMaterial.dispose();

                renderer.dispose();
            }
        );

    }
);
