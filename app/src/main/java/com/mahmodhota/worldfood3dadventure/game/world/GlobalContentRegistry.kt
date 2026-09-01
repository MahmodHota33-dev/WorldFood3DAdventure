package com.mahmodhota.worldfood3dadventure.game.world

import com.mahmodhota.worldfood3dadventure.game.world.model.Continent
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryDefinition
import com.mahmodhota.worldfood3dadventure.game.world.model.CountryMetadata
import com.mahmodhota.worldfood3dadventure.game.match3.model.Match3LevelDefinition
import com.mahmodhota.worldfood3dadventure.game.match3.model.FoodTileType
import com.mahmodhota.worldfood3dadventure.game.match3.model.LevelGoal

/**
 * Extension to LevelRegistry that manages the expanded global content.
 */
object GlobalContentRegistry {

    val EXTRA_COUNTRIES = listOf(
        // North America
        CountryMetadata("usa", "USA", "US", "🇺🇸", "The land of big burgers and iconic skyline diners.", Continent.NORTH_AMERICA),
        CountryMetadata("canada", "Canada", "CA", "🇨🇦", "Maple syrup, poutine, and vast wilderness.", Continent.NORTH_AMERICA),
        
        // South America
        CountryMetadata("brazil", "Brazil", "BR", "🇧🇷", "Vibrant festivals and the heart of Samba cuisine.", Continent.SOUTH_AMERICA),
        CountryMetadata("argentina", "Argentina", "AR", "🇦🇷", "A paradise for steak lovers and tango fans.", Continent.SOUTH_AMERICA),
        CountryMetadata("peru", "Peru", "PE", "🇵🇪", "Discover the rich history and unique flavors of the Andes.", Continent.SOUTH_AMERICA),
        
        // Europe
        CountryMetadata("uk", "UK", "GB", "🇬🇧", "Traditional tea, castles, and classic fish & chips.", Continent.EUROPE),
        CountryMetadata("greece", "Greece", "GR", "🇬🇷", "Timeless white-washed houses and fresh Mediterranean flavors.", Continent.EUROPE),
        CountryMetadata("sweden", "Sweden", "SE", "🇸🇪", "Forest-inspired dishes and modern Scandinavian design.", Continent.EUROPE),
        CountryMetadata("portugal", "Portugal", "PT", "🇵🇹", "A coastal journey through seafood and traditional pastries.", Continent.EUROPE),
        CountryMetadata("switzerland", "Switzerland", "CH", "🇨🇭", "The peak of chocolate and cheese experiences.", Continent.EUROPE),
        CountryMetadata("russia", "Russia", "RU", "🇷🇺", "A vast exploration of diverse traditions and hearty stews.", Continent.EUROPE),
        
        // Asia
        CountryMetadata("china", "China", "CN", "🇨🇳", "A millennium of culinary history in every bite.", Continent.ASIA),
        CountryMetadata("korea", "Korea", "KR", "🇰🇷", "The perfect balance of spice, ferment, and soul food.", Continent.ASIA),
        CountryMetadata("thailand", "Thailand", "TH", "🇹🇭", "A symphony of sweet, sour, salty, and spicy.", Continent.ASIA),
        CountryMetadata("vietnam", "Vietnam", "VN", "🇻🇳", "Fragrant herbs and fresh ingredients from the Mekong delta.", Continent.ASIA),
        CountryMetadata("india", "India", "IN", "🇮🇳", "A colorful explosion of spices and regional specialties.", Continent.ASIA),
        CountryMetadata("indonesia", "Indonesia", "ID", "🇮🇩", "Island flavors and the world's most aromatic spices.", Continent.ASIA),
        CountryMetadata("philippines", "Philippines", "PH", "🇵🇭", "Unique fusion of Spanish and Asian island influences.", Continent.ASIA),
        
        // Middle East & Turkey
        CountryMetadata("turkey", "Turkey", "TR", "🇹🇷", "Where East meets West in a feast of kebabs and delights.", Continent.ASIA),
        CountryMetadata("egypt", "Egypt", "EG", "🇪🇬", "Flavors as ancient as the pyramids themselves.", Continent.AFRICA),
        CountryMetadata("morocco", "Morocco", "MA", "🇲🇦", "Tagines, tea, and the bustling markets of Marrakech.", Continent.AFRICA),
        CountryMetadata("saudi", "Saudi Arabia", "SA", "🇸🇦", "Rich heritage and generous hospitality of the desert.", Continent.ASIA),
        CountryMetadata("israel", "Israel", "IL", "🇮🇱", "Modern Mediterranean fusion and vibrant market foods.", Continent.ASIA),
        
        // Africa
        CountryMetadata("nigeria", "Nigeria", "NG", "🇳🇬", "The giant of Africa, with bold flavors and jollof rice.", Continent.AFRICA),
        CountryMetadata("south_africa", "South Africa", "ZA", "🇿🇦", "A rainbow nation of cultures and diverse braai dishes.", Continent.AFRICA),
        CountryMetadata("ethiopia", "Ethiopia", "ET", "🇪🇹", "Unique injera bread and aromatic coffee ceremonies.", Continent.AFRICA),
        CountryMetadata("kenya", "Kenya", "KE", "🇰🇪", "Wild landscapes and traditional East African staples.", Continent.AFRICA),
        
        // Oceania
        CountryMetadata("australia", "Australia", "AU", "🇦🇺", "Golden beaches and a relaxed outback BBQ culture.", Continent.OCEANIA),

        // Additional set
        CountryMetadata("netherlands", "Netherlands", "NL", "🇳🇱", "Windmills, canals, and delicious cheeses.", Continent.EUROPE),
        CountryMetadata("belgium", "Belgium", "BE", "🇧🇪", "The world's best chocolate, waffles, and fries.", Continent.EUROPE),
        CountryMetadata("austria", "Austria", "AT", "🇦🇹", "Classical music, Alpine peaks, and schnitzel.", Continent.EUROPE),
        CountryMetadata("norway", "Norway", "NO", "🇳🇴", "Fjords, Vikings, and fresh salmon from the North.", Continent.EUROPE),
        CountryMetadata("denmark", "Denmark", "DK", "🇩🇰", "The home of Hygge, LEGO, and open-faced sandwiches.", Continent.EUROPE),
        CountryMetadata("finland", "Finland", "FI", "🇫🇮", "Northern lights and the happiest coffee culture.", Continent.EUROPE),
        CountryMetadata("poland", "Poland", "PL", "🇵🇱", "Rich history and hearty dumplings known as pierogi.", Continent.EUROPE),
        CountryMetadata("ukraine", "Ukraine", "UA", "🇺🇦", "Sunflowers, heritage, and warming borscht soup.", Continent.EUROPE),
        CountryMetadata("ireland", "Ireland", "IE", "🇮🇪", "Emerald isles, folklore, and comforting stews.", Continent.EUROPE),
        CountryMetadata("colombia", "Colombia", "CO", "🇨🇴", "The heart of coffee and vibrant tropical fruits.", Continent.SOUTH_AMERICA),
        CountryMetadata("chile", "Chile", "CL", "🇨🇱", "Stretching from the Andes to the Pacific coast.", Continent.SOUTH_AMERICA),
        CountryMetadata("malaysia", "Malaysia", "MY", "🇲🇾", "A melting pot of Malay, Chinese, and Indian flavors.", Continent.ASIA),
        CountryMetadata("singapore", "Singapore", "SG", "🇸🇬", "The world's street food capital in a garden city.", Continent.ASIA),
        CountryMetadata("new_zealand", "New Zealand", "NZ", "🇳🇿", "Stunning nature and unique Maori traditions.", Continent.OCEANIA),
        CountryMetadata("pakistan", "Pakistan", "PK", "🇵🇰", "Spicy kebabs and rich heritage of the Indus Valley.", Continent.ASIA),
        CountryMetadata("iran", "Iran", "IR", "🇮🇷", "Ancient Persian flavors of saffron and rosewater.", Continent.ASIA),
        CountryMetadata("iraq", "Iraq", "IQ", "🇮🇶", "The cradle of civilization and legendary hospitality.", Continent.ASIA),
        CountryMetadata("algeria", "Algeria", "DZ", "🇩🇿", "Where the Sahara meets the blue Mediterranean.", Continent.AFRICA),
        CountryMetadata("tanzania", "Tanzania", "TZ", "🇹🇿", "Serengeti plains and the aromatic spice island of Zanzibar.", Continent.AFRICA),
        CountryMetadata("venezuela", "Venezuela", "VE", "🇻🇪", "Lush rainforests and the famous arepas.", Continent.SOUTH_AMERICA),

        // NEW COUNTRIES (P10 Expansion: 55 -> 213)
        CountryMetadata("iceland", "Iceland", "IS", "🇮🇸", "The land of fire and ice with unique volcanic soil flavors.", Continent.EUROPE),
        CountryMetadata("hungary", "Hungary", "HU", "🇭🇺", "Home of paprika and rich, hearty goulash stews.", Continent.EUROPE),
        CountryMetadata("czech_republic", "Czech Republic", "CZ", "🇨🇿", "Famous for its historic breweries and traditional roast pork.", Continent.EUROPE),
        CountryMetadata("romania", "Romania", "RO", "🇷🇴", "Transylvanian traditions and delicious stuffed cabbage rolls.", Continent.EUROPE),
        CountryMetadata("bulgaria", "Bulgaria", "BG", "🇧🇬", "Known for fresh salads and world-famous yogurt.", Continent.EUROPE),
        CountryMetadata("croatia", "Croatia", "HR", "🇭🇷", "Adriatic coastal gems and Mediterranean seafood delights.", Continent.EUROPE),
        CountryMetadata("serbia", "Serbia", "RS", "🇷🇸", "Hearty grilled meats and vibrant Balkan hospitality.", Continent.EUROPE),
        CountryMetadata("slovakia", "Slovakia", "SK", "🇸🇰", "Traditional bryndzové halušky in the heart of the Tatras.", Continent.EUROPE),
        CountryMetadata("lithuania", "Lithuania", "LT", "🇱🇹", "Unique potato dishes and forest-inspired Baltic flavors.", Continent.EUROPE),
        CountryMetadata("latvia", "Latvia", "LV", "🇱🇻", "Rye bread, smoked fish, and Baltic coastal traditions.", Continent.EUROPE),
        CountryMetadata("kazakhstan", "Kazakhstan", "KZ", "🇰🇿", "Central Asian nomadic traditions and hearty meat dishes.", Continent.ASIA),
        CountryMetadata("uzbekistan", "Uzbekistan", "UZ", "🇺🇿", "The silk road's finest pilaf and aromatic spices.", Continent.ASIA),
        CountryMetadata("mongolia", "Mongolia", "MN", "🇲🇳", "Traditional dumplings and milk tea from the vast steppes.", Continent.ASIA),
        CountryMetadata("nepal", "Nepal", "NP", "🇳🇵", "Himalayan momo dumplings and spicy mountain flavors.", Continent.ASIA),
        CountryMetadata("bangladesh", "Bangladesh", "BD", "🇧🇩", "Riches of the delta with spicy fish curry and rice.", Continent.ASIA),
        CountryMetadata("sri_lanka", "Sri Lanka", "LK", "🇱🇰", "The pearl of the Indian ocean, famous for tea and spices.", Continent.ASIA),
        CountryMetadata("cambodia", "Cambodia", "KH", "🇰🇭", "Ancient Angkor traditions and flavorful Khmer curries.", Continent.ASIA),
        CountryMetadata("laos", "Laos", "LA", "🇱🇦", "Aromatic herbs and sticky rice from the Mekong river.", Continent.ASIA),
        CountryMetadata("myanmar", "Myanmar", "MM", "🇲🇲", "Unique tea leaf salads and diverse regional specialties.", Continent.ASIA),
        CountryMetadata("jordan", "Jordan", "JO", "🇯🇴", "Legendary hospitality and the famous mansaf feast.", Continent.ASIA),
        CountryMetadata("ghana", "Ghana", "GH", "🇬🇭", "The golden coast with delicious jollof and cocoa.", Continent.AFRICA),
        CountryMetadata("ivory_coast", "Ivory Coast", "CI", "🇨🇮", "Flavorful sauces and world-leading cocoa production.", Continent.AFRICA),
        CountryMetadata("senegal", "Senegal", "SN", "🇸🇳", "Teranga hospitality and classic West African rice dishes.", Continent.AFRICA),
        CountryMetadata("uganda", "Uganda", "UG", "🇺🇬", "The pearl of Africa with diverse tropical fruits and matooke.", Continent.AFRICA),
        CountryMetadata("rwanda", "Rwanda", "RW", "🇷🇼", "Land of a thousand hills and premium mountain coffee.", Continent.AFRICA),
        CountryMetadata("zambia", "Zambia", "ZM", "🇿🇲", "Traditional nshima and wildlife-rich savannah flavors.", Continent.AFRICA),
        CountryMetadata("zimbabwe", "Zimbabwe", "ZW", "🇿🇼", "Rich heritage and iconic Victoria Falls regional dishes.", Continent.AFRICA),
        CountryMetadata("madagascar", "Madagascar", "MG", "🇲🇬", "Unique island flavors including vanilla and exotic spices.", Continent.AFRICA),
        CountryMetadata("tunisia", "Tunisia", "TN", "🇹🇳", "Mediterranean breeze and spicy harissa-infused dishes.", Continent.AFRICA),
        CountryMetadata("libya", "Libya", "LY", "🇱🇾", "Ancient desert oases and Mediterranean coastal flavors.", Continent.AFRICA),
        CountryMetadata("costa_rica", "Costa Rica", "CR", "🇨🇷", "Pura Vida flavors from the lush tropical rainforests.", Continent.NORTH_AMERICA),
        CountryMetadata("panama", "Panama", "PA", "🇵🇦", "A bridge of flavors between two great oceans.", Continent.NORTH_AMERICA),
        CountryMetadata("guatemala", "Guatemala", "GT", "🇬🇹", "Heart of the Mayan world with ancient corn traditions.", Continent.NORTH_AMERICA),
        CountryMetadata("cuba", "Cuba", "CU", "🇨🇺", "Vibrant Caribbean soul and world-famous island cuisine.", Continent.NORTH_AMERICA),
        CountryMetadata("jamaica", "Jamaica", "JM", "🇯🇲", "Island rhythms and spicy jerk chicken from the Caribbean.", Continent.NORTH_AMERICA),
        CountryMetadata("dominican_republic", "Dominican Republic", "DO", "🇩🇴", "Golden beaches and traditional Caribbean comfort foods.", Continent.NORTH_AMERICA),
        CountryMetadata("haiti", "Haiti", "HT", "🇭🇹", "Rich history and unique Afro-Caribbean culinary fusion.", Continent.NORTH_AMERICA),
        CountryMetadata("bahamas", "Bahamas", "BS", "🇧🇸", "Crystal clear waters and delicious conch specialties.", Continent.NORTH_AMERICA),
        CountryMetadata("greenland", "Greenland", "GL", "🇬🇱", "Arctic traditions and the freshest cold-water seafood.", Continent.NORTH_AMERICA),
        CountryMetadata("puerto_rico", "Puerto Rico", "PR", "PR", "The island of enchantment with vibrant Latin flavors.", Continent.NORTH_AMERICA),
        CountryMetadata("ecuador", "Ecuador", "EC", "🇪🇨", "Equatorial diversity from the Andes to the Galapagos.", Continent.SOUTH_AMERICA),
        CountryMetadata("bolivia", "Bolivia", "BO", "🇧🇴", "High-altitude traditions and unique Andean ingredients.", Continent.SOUTH_AMERICA),
        CountryMetadata("paraguay", "Paraguay", "PY", "🇵🇾", "Heart of South America with traditional yerba mate.", Continent.SOUTH_AMERICA),
        CountryMetadata("uruguay", "Uruguay", "UY", "🇺🇾", "Relaxed coastal lifestyle and premium grilled meats.", Continent.SOUTH_AMERICA),
        CountryMetadata("guyana", "Guyana", "GY", "🇬🇾", "A unique blend of Caribbean and South American flavors.", Continent.SOUTH_AMERICA),
        CountryMetadata("fiji", "Fiji", "FJ", "🇫🇯", "Tropical paradise with fresh seafood and coconut flavors.", Continent.OCEANIA),
        CountryMetadata("papua_new_guinea", "Papua New Guinea", "PG", "🇵🇬", "Vast cultural diversity and traditional earthen oven cooking.", Continent.OCEANIA),
        CountryMetadata("samoa", "Samoa", "🇼🇸", "WS", "Pacific island traditions and fresh tropical ingredients.", Continent.OCEANIA),
        CountryMetadata("tonga", "Tonga", "TO", "🇹🇴", "The friendly islands with unique Polynesian heritage.", Continent.OCEANIA),
        CountryMetadata("vanuatu", "Vanuatu", "VU", "🇻🇺", "Untouched natural beauty and traditional island feasts.", Continent.OCEANIA),

        // P10 Expansion (105 -> 213)
        CountryMetadata("albania", "Albania", "AL", "🇦🇱", "Balkan flavors and stunning Adriatic views.", Continent.EUROPE),
        CountryMetadata("andorra", "Andorra", "AD", "🇦🇩", "Pyrenean mountain traditions and hearty stews.", Continent.EUROPE),
        CountryMetadata("armenia", "Armenia", "AM", "🇦🇲", "Ancient history and aromatic Caucasian cuisine.", Continent.EUROPE),
        CountryMetadata("azerbaijan", "Azerbaijan", "AZ", "🇦🇿", "Where East meets West in a fusion of fire and flavor.", Continent.EUROPE),
        CountryMetadata("belarus", "Belarus", "BY", "🇧🇾", "Hearty potato dishes and Eastern European heritage.", Continent.EUROPE),
        CountryMetadata("bosnia", "Bosnia", "BA", "🇧🇦", "Rich Balkan coffee culture and grilled specialties.", Continent.EUROPE),
        CountryMetadata("cyprus", "Cyprus", "CY", "🇨🇾", "Mediterranean island flavors and Halloumi cheese.", Continent.EUROPE),
        CountryMetadata("estonia", "Estonia", "EE", "🇪🇪", "Modern Baltic cuisine inspired by deep forests.", Continent.EUROPE),
        CountryMetadata("georgia", "Georgia", "GE", "🇬🇪", "The cradle of wine and unique Khachapuri bread.", Continent.EUROPE),
        CountryMetadata("kosovo", "Kosovo", "XK", "🇽🇰", "Vibrant Balkan traditions and welcoming hospitality.", Continent.EUROPE),
        CountryMetadata("liechtenstein", "Liechtenstein", "LI", "🇱🇮", "Alpine peaks and traditional mountain flavors.", Continent.EUROPE),
        CountryMetadata("luxembourg", "Luxembourg", "LU", "🇱🇺", "A rich blend of French and German culinary arts.", Continent.EUROPE),
        CountryMetadata("malta", "Malta", "MT", "🇲🇹", "Sun-drenched Mediterranean flavors and ancient history.", Continent.EUROPE),
        CountryMetadata("moldova", "Moldova", "MD", "🇲🇩", "Renowned vineyards and traditional Eastern stews.", Continent.EUROPE),
        CountryMetadata("monaco", "Monaco", "MC", "🇲🇨", "Riviera luxury and fine Mediterranean dining.", Continent.EUROPE),
        CountryMetadata("montenegro", "Montenegro", "ME", "🇲🇪", "Coastal seafood and rugged mountain traditions.", Continent.EUROPE),
        CountryMetadata("north_macedonia", "North Macedonia", "MK", "🇲🇰", "Timeless Balkan heritage and rich farm flavors.", Continent.EUROPE),
        CountryMetadata("san_marino", "San Marino", "SM", "🇸🇲", "Historic hilltop views and Italian-inspired dishes.", Continent.EUROPE),
        CountryMetadata("slovenia", "Slovenia", "SI", "🇸🇮", "Where the Alps meet the Mediterranean breeze.", Continent.EUROPE),
        CountryMetadata("vatican_city", "Vatican City", "VA", "🇻🇦", "The spiritual heart with timeless Roman flavors.", Continent.EUROPE),
        CountryMetadata("afghanistan", "Afghanistan", "AF", "🇦🇫", "Aromatic rice and ancient Silk Road spices.", Continent.ASIA),
        CountryMetadata("bahrain", "Bahrain", "BH", "🇧🇭", "Island traditions and modern Gulf flavors.", Continent.ASIA),
        CountryMetadata("bhutan", "Bhutan", "BT", "🇧🇹", "Spicy Himalayan flavors and the Land of Happiness.", Continent.ASIA),
        CountryMetadata("brunei", "Brunei", "BN", "🇧🇳", "Royal traditions and rich Southeast Asian spices.", Continent.ASIA),
        CountryMetadata("east_timor", "East Timor", "TL", "🇹🇱", "Coastal island heritage and unique coffee culture.", Continent.ASIA),
        CountryMetadata("kuwait", "Kuwait", "KW", "🇰🇼", "Vibrant markets and traditional desert hospitality.", Continent.ASIA),
        CountryMetadata("kyrgyzstan", "Kyrgyzstan", "KG", "🇰🇬", "Nomadic traditions and hearty mountain dishes.", Continent.ASIA),
        CountryMetadata("lebanon", "Lebanon", "LB", "🇱🇧", "A masterpiece of Mediterranean and Levantine flavors.", Continent.ASIA),
        CountryMetadata("maldives", "Maldives", "MV", "🇲🇻", "Tropical paradise with fresh seafood and coconuts.", Continent.ASIA),
        CountryMetadata("north_korea", "North Korea", "KP", "🇰🇵", "Timeless traditions from the Hermit Kingdom.", Continent.ASIA),
        CountryMetadata("oman", "Oman", "OM", "🇴🇲", "Desert oases and legendary Frankincense routes.", Continent.ASIA),
        CountryMetadata("palestine", "Palestine", "PS", "🇵🇸", "Rich heritage and olive groves of the Levant.", Continent.ASIA),
        CountryMetadata("qatar", "Qatar", "QA", "🇶🇦", "Modern skyline and traditional pearl diving roots.", Continent.ASIA),
        CountryMetadata("syria", "Syria", "SY", "🇸🇾", "Ancient Damascus flavors and rich culinary history.", Continent.ASIA),
        CountryMetadata("taiwan", "Taiwan", "TW", "🇹🇼", "Vibrant night markets and the home of bubble tea.", Continent.ASIA),
        CountryMetadata("tajikistan", "Tajikistan", "TJ", "🇹🇯", "High-altitude peaks and traditional Pamir flavors.", Continent.ASIA),
        CountryMetadata("turkmenistan", "Turkmenistan", "TM", "🇹🇲", "Ancient Merv history and desert nomadic life.", Continent.ASIA),
        CountryMetadata("uae", "UAE", "AE", "🇦🇪", "Global fusion and visionary desert architecture.", Continent.ASIA),
        CountryMetadata("yemen", "Yemen", "YE", "🇾🇪", "Ancient coffee heritage and aromatic spice blends.", Continent.ASIA),
        CountryMetadata("macau", "Macau", "MO", "🇲🇴", "A unique blend of Portuguese and Chinese heritage.", Continent.ASIA),
        CountryMetadata("angola", "Angola", "AO", "🇦🇴", "Coastal rhythms and rich West African flavors.", Continent.AFRICA),
        CountryMetadata("benin", "Benin", "BJ", "🇧🇯", "The heart of West African history and culture.", Continent.AFRICA),
        CountryMetadata("botswana", "Botswana", "BW", "🇧🇼", "Kalahari desert beauty and rich wildlife heritage.", Continent.AFRICA),
        CountryMetadata("burkina_faso", "Burkina Faso", "BF", "🇧🇫", "Land of upright people and vibrant music.", Continent.AFRICA),
        CountryMetadata("burundi", "Burundi", "BI", "🇧🇮", "Heart of Africa with lush green hills and tea.", Continent.AFRICA),
        CountryMetadata("cabo_verde", "Cabo Verde", "CV", "🇨🇻", "Atlantic island soul and Creole traditions.", Continent.AFRICA),
        CountryMetadata("cameroon", "Cameroon", "CM", "🇨🇲", "Africa in miniature with diverse landscapes.", Continent.AFRICA),
        CountryMetadata("central_african_republic", "CAR", "CF", "🇨🇫", "Wild rainforests and deep continental heart.", Continent.AFRICA),
        CountryMetadata("chad", "Chad", "TD", "🇹🇩", "Sahara beauty and ancient Lake Chad traditions.", Continent.AFRICA),
        CountryMetadata("comoros", "Comoros", "KM", "🇰🇲", "Perfumed islands of vanilla and ylang-ylang.", Continent.AFRICA),
        CountryMetadata("drc", "DRC", "CD", "🇨🇩", "The massive Congo basin and rhythmic soul.", Continent.AFRICA),
        CountryMetadata("republic_congo", "Congo", "CG", "🇨🇬", "Pristine rainforests and vibrant riverside life.", Continent.AFRICA),
        CountryMetadata("djibouti", "Djibouti", "DJ", "🇩🇯", "Volcanic landscapes and the Horn of Africa gateway.", Continent.AFRICA),
        CountryMetadata("equatorial_guinea", "Equatorial Guinea", "GQ", "🇬🇶", "Tropical forests and unique Spanish heritage.", Continent.AFRICA),
        CountryMetadata("eritrea", "Eritrea", "ER", "🇪🇷", "Red Sea coast and unique architectural charm.", Continent.AFRICA),
        CountryMetadata("eswatini", "Eswatini", "SZ", "🇸🇿", "Timeless kingdom and colorful cultural festivals.", Continent.AFRICA),
        CountryMetadata("gabon", "Gabon", "GA", "🇬🇦", "Rich biodiversity and pristine coastal parks.", Continent.AFRICA),
        CountryMetadata("gambia", "Gambia", "GM", "🇬🇲", "The smiling coast of West Africa.", Continent.AFRICA),
        CountryMetadata("guinea", "Guinea", "GN", "🇬🇳", "Water tower of West Africa with rich music.", Continent.AFRICA),
        CountryMetadata("guinea_bissau", "Guinea-Bissau", "GW", "🇬🇼", "Archipelago beauty and unique Creole culture.", Continent.AFRICA),
        CountryMetadata("lesotho", "Lesotho", "LS", "🇱🇸", "Kingdom in the sky with high mountain peaks.", Continent.AFRICA),
        CountryMetadata("liberia", "Liberia", "LR", "🇱🇷", "History of freedom and lush coastal rainforests.", Continent.AFRICA),
        CountryMetadata("malawi", "Malawi", "MW", "🇲🇼", "The warm heart of Africa and Lake Malawi.", Continent.AFRICA),
        CountryMetadata("mali", "Mali", "ML", "🇲🇱", "Ancient Timbuktu history and desert rhythms.", Continent.AFRICA),
        CountryMetadata("mauritania", "Mauritania", "MR", "🇲🇷", "Where the Sahara sands meet the Atlantic.", Continent.AFRICA),
        CountryMetadata("mauritius", "Mauritius", "MU", "🇲🇺", "Tropical paradise with a melting pot of flavors.", Continent.AFRICA),
        CountryMetadata("namibia", "Namibia", "NA", "🇳🇦", "Stunning desert dunes and vast landscapes.", Continent.AFRICA),
        CountryMetadata("niger", "Niger", "NE", "🇳🇪", "Sahara heritage and ancient caravan routes.", Continent.AFRICA),
        CountryMetadata("sao_tome", "Sao Tome", "ST", "🇸🇹", "Chocolate islands and lush tropical beauty.", Continent.AFRICA),
        CountryMetadata("seychelles", "Seychelles", "SC", "🇸🇨", "Pristine beaches and unique island wildlife.", Continent.AFRICA),
        CountryMetadata("kiribati", "Kiribati", "KI", "🇰🇮", "Pacific atolls and the first to see the sun.", Continent.OCEANIA),
        CountryMetadata("marshall_islands", "Marshall Islands", "MH", "🇲🇭", "Crystal lagoons and Pacific island heritage.", Continent.OCEANIA),
        CountryMetadata("micronesia", "Micronesia", "FM", "🇫🇲", "Island wonders across the vast Pacific.", Continent.OCEANIA),
        CountryMetadata("nauru", "Nauru", "NR", "🇳🇷", "Small island beauty and unique phosphate history.", Continent.OCEANIA),
        CountryMetadata("palau", "Palau", "PW", "🇵🇼", "Pristine marine life and rock island wonders.", Continent.OCEANIA),
        CountryMetadata("solomon_islands", "Solomon Islands", "SB", "🇸🇧", "Unspoiled nature and rich Melanesian culture.", Continent.OCEANIA),
        CountryMetadata("tuvalu", "Tuvalu", "TV", "🇹🇻", "Remote island tranquility in the deep Pacific.", Continent.OCEANIA),
        CountryMetadata("cook_islands", "Cook Islands", "CK", "🇨🇰", "Polynesian soul and stunning turquoise waters.", Continent.OCEANIA),
        CountryMetadata("niue", "Niue", "NU", "🇳🇺", "The rock of Polynesia with clear coastal caves.", Continent.OCEANIA),
        CountryMetadata("french_polynesia", "French Polynesia", "PF", "🇵🇫", "Bora Bora dreams and vibrant Tahitian life.", Continent.OCEANIA),
        CountryMetadata("antigua_barbuda", "Antigua & Barbuda", "AG", "🇦🇬", "365 beaches and vibrant Caribbean soul.", Continent.NORTH_AMERICA),
        CountryMetadata("barbados", "Barbados", "BB", "🇧🇧", "Crystal waters and the home of Caribbean rum.", Continent.NORTH_AMERICA),
        CountryMetadata("belize", "Belize", "BZ", "🇧🇿", "Mayan ruins and the Great Blue Hole.", Continent.NORTH_AMERICA),
        CountryMetadata("dominica", "Dominica", "DM", "🇩🇲", "Nature island with lush peaks and waterfalls.", Continent.NORTH_AMERICA),
        CountryMetadata("el_salvador", "El Salvador", "SV", "🇸🇻", "Volcanoes, surfing, and delicious pupusas.", Continent.NORTH_AMERICA),
        CountryMetadata("grenada", "Grenada", "GD", "🇬🇩", "The Spice Isle with aromatic nutmeg and cocoa.", Continent.NORTH_AMERICA),
        CountryMetadata("honduras", "Honduras", "HN", "🇭🇳", "Ancient Copan ruins and Caribbean coastlines.", Continent.NORTH_AMERICA),
        CountryMetadata("nicaragua", "Nicaragua", "NI", "🇳🇮", "Land of lakes and volcanoes with rich history.", Continent.NORTH_AMERICA),
        CountryMetadata("st_kitts_nevis", "St. Kitts & Nevis", "KN", "🇰🇳", "Lush plantations and twin island beauty.", Continent.NORTH_AMERICA),
        CountryMetadata("st_lucia", "St. Lucia", "LC", "🇱🇨", "Iconic Piton peaks and tropical rainforests.", Continent.NORTH_AMERICA),
        CountryMetadata("st_vincent", "St. Vincent", "VC", "🇻🇨", "Untouched Caribbean charm and island chains.", Continent.NORTH_AMERICA),
        CountryMetadata("trinidad_tobago", "Trinidad & Tobago", "TT", "🇹🇹", "Carnival rhythms and rich fusion flavors.", Continent.NORTH_AMERICA),
        CountryMetadata("bermuda", "Bermuda", "BM", "🇧🇲", "Pink sand beaches and Atlantic charm.", Continent.NORTH_AMERICA),
        CountryMetadata("cayman_islands", "Cayman Islands", "KY", "🇰🇾", "Crystal clear waters and world-class diving.", Continent.NORTH_AMERICA),
        CountryMetadata("bvi", "BVI", "VG", "🇻🇬", "Sailing paradise across pristine island cays.", Continent.NORTH_AMERICA),
        CountryMetadata("suriname", "Suriname", "SR", "🇸🇷", "Unique melting pot of South American cultures.", Continent.SOUTH_AMERICA),
        CountryMetadata("falkland_islands", "Falkland Islands", "FK", "🇫🇰", "Rugged islands and unique Southern wildlife.", Continent.SOUTH_AMERICA),
        CountryMetadata("french_guiana", "French Guiana", "GF", "🇬🇫", "Amazonian gateway and European space center.", Continent.SOUTH_AMERICA),
        CountryMetadata("sierra_leone", "Sierra Leone", "🇸🇱", "🇸🇱", "Lion mountains and stunning West African coast.", Continent.AFRICA),
        CountryMetadata("somalia", "Somalia", "SO", "🇸🇴", "Horn of Africa heritage and long coastlines.", Continent.AFRICA),
        CountryMetadata("south_sudan", "South Sudan", "SS", "🇸🇸", "The world's youngest nation with rich traditions.", Continent.AFRICA),
        CountryMetadata("togo", "Togo", "TG", "🇹🇬", "Vibrant markets and traditional Voodoo heritage.", Continent.AFRICA),
        CountryMetadata("reunion", "Reunion", "RE", "🇷🇪", "Volcanic peaks and a French-Creole blend.", Continent.AFRICA),
        CountryMetadata("mayotte", "Mayotte", "YT", "🇾🇹", "Lagoon beauty and Indian Ocean island soul.", Continent.AFRICA),
        CountryMetadata("st_helena", "St. Helena", "SH", "🇸🇭", "Remote Atlantic history and rugged beauty.", Continent.AFRICA),
        CountryMetadata("western_sahara", "Western Sahara", "EH", "🇪🇭", "Desert sands and rich nomadic heritage.", Continent.AFRICA),
        CountryMetadata("montserrat", "Montserrat", "MS", "🇲🇸", "The emerald isle of the Caribbean.", Continent.NORTH_AMERICA),
        CountryMetadata("turks_caicos", "Turks & Caicos", "TC", "🇹🇨", "Pristine beaches and turquoise Atlantic waters.", Continent.NORTH_AMERICA)
    )

    /** Generates a standard set of levels for a newly added country. */
    fun generateLevels(countryId: String, representativeFoods: List<FoodTileType>): List<Match3LevelDefinition> {
        return (1..15).map { i ->
            // P9-C: Rotate foods to maintain identity but provide variation
            val levelFoodPool = if (representativeFoods.size >= 5) {
                when {
                    i <= 5 -> representativeFoods.take(5)
                    i <= 10 -> (representativeFoods.take(3) + representativeFoods.drop(5).take(2))
                    else -> representativeFoods.shuffled().take(5)
                }
            } else representativeFoods

            val mainFood = levelFoodPool.getOrElse((i - 1) % levelFoodPool.size) { levelFoodPool.first() }
            
            // P9 Progressive Difficulty Curve
            val baseMoves = when {
                i <= 5 -> 30 - i 
                i <= 10 -> 25 - (i - 5)
                else -> 22 - (i - 10) 
            }
            
            val baseTarget = when {
                i <= 5 -> 1500 + i * 400
                i <= 10 -> 4000 + (i - 5) * 800
                else -> 9000 + (i - 10) * 1500
            }

            Match3LevelDefinition(
                countryId = countryId,
                levelNumber = i,
                moves = baseMoves.coerceAtLeast(12),
                goals = listOf(
                    LevelGoal.ScoreTarget(baseTarget),
                    LevelGoal.CollectFood(mainFood, 10 + i * 2)
                ),
                allowedTiles = (levelFoodPool + listOf(FoodTileType.EGG, FoodTileType.MILK, FoodTileType.APPLE, FoodTileType.POTATO)).distinct().take(6),
                title = if (i == 15) "Grand Culinary Finale" else null
            )
        }
    }

    /** P9-C: Comprehensive land-specific food themes mapping. */
    fun getExtraFoods(countryId: String): List<FoodTileType> = when(countryId) {
        "usa" -> listOf(FoodTileType.BURGER, FoodTileType.FRIES, FoodTileType.HOT_DOG, FoodTileType.DONUT, FoodTileType.STEAK, FoodTileType.CORN)
        "canada" -> listOf(FoodTileType.PANCAKE, FoodTileType.POTATO, FoodTileType.BREAD, FoodTileType.FISH, FoodTileType.APPLE, FoodTileType.MILK)
        "uk" -> listOf(FoodTileType.FISH_AND_CHIPS, FoodTileType.SCONE, FoodTileType.TEA, FoodTileType.POT_PIE, FoodTileType.BREAD, FoodTileType.STEAK)
        "china" -> listOf(FoodTileType.DIM_SUM, FoodTileType.DUMPLING, FoodTileType.FRIED_RICE, FoodTileType.PEKING_DUCK, FoodTileType.SPRING_ROLL, FoodTileType.RICE)
        "india" -> listOf(FoodTileType.CURRY, FoodTileType.NAAN, FoodTileType.BIRYANI, FoodTileType.TANDOORI_CHICKEN, FoodTileType.GULAB_JAMUN, FoodTileType.TEA)
        "brazil" -> listOf(FoodTileType.FEIJOADA, FoodTileType.BRIGADEIRO, FoodTileType.COXINHA, FoodTileType.PADE_QUEIJO, FoodTileType.COFFEE, FoodTileType.CORN)
        "egypt" -> listOf(FoodTileType.KOSHARY, FoodTileType.FALAFEL, FoodTileType.SHAWARMA, FoodTileType.BAKLAVA, FoodTileType.BREAD, FoodTileType.TOMATO)
        "greece" -> listOf(FoodTileType.GYROS, FoodTileType.MOUSSAKA, FoodTileType.FETA, FoodTileType.OLIVES, FoodTileType.TOMATO, FoodTileType.BASIL)
        "thailand" -> listOf(FoodTileType.PAD_THAI, FoodTileType.TOM_YUM, FoodTileType.MANGO_STICKY_RICE, FoodTileType.RICE, FoodTileType.SPRING_ROLL, FoodTileType.FISH)
        "korea" -> listOf(FoodTileType.KIMCHI, FoodTileType.BIBIMBAP, FoodTileType.BULGOGI, FoodTileType.RICE, FoodTileType.EGG, FoodTileType.CHICKEN)
        "turkey" -> listOf(FoodTileType.KEBAB, FoodTileType.TURKISH_DELIGHT, FoodTileType.KOFTE, FoodTileType.COFFEE, FoodTileType.BREAD, FoodTileType.OLIVES)
        "australia" -> listOf(FoodTileType.STEAK, FoodTileType.FISH, FoodTileType.BURGER, FoodTileType.APPLE, FoodTileType.POTATO, FoodTileType.MILK)
        "new_zealand" -> listOf(FoodTileType.FISH, FoodTileType.STEAK, FoodTileType.APPLE, FoodTileType.MILK, FoodTileType.BREAD, FoodTileType.POTATO)
        "ireland" -> listOf(FoodTileType.FISH_AND_CHIPS, FoodTileType.STEAK, FoodTileType.POTATO, FoodTileType.BREAD, FoodTileType.TEA)
        "portugal" -> listOf(FoodTileType.PAELLA, FoodTileType.FISH, FoodTileType.BREAD, FoodTileType.TOMATO, FoodTileType.CHEESE)

        // Northern Europe Archetype
        "iceland", "norway", "denmark", "finland", "sweden", "greenland", "estonia" -> listOf(FoodTileType.FISH, FoodTileType.POTATO, FoodTileType.BREAD, FoodTileType.COFFEE, FoodTileType.MILK, FoodTileType.CHEESE, FoodTileType.APPLE)
        
        // Central/Western Europe Archetype
        "netherlands", "belgium", "austria", "switzerland", "luxembourg", "liechtenstein", "monaco" -> listOf(FoodTileType.CHEESE, FoodTileType.BREAD, FoodTileType.POTATO, FoodTileType.APPLE, FoodTileType.MILK, FoodTileType.BLACK_FOREST_CAKE)

        // Eastern Europe / Balkan Archetype
        "ukraine", "poland", "russia", "lithuania", "latvia", "czech_republic", "slovakia", "hungary", "romania", "bulgaria", "serbia", "croatia", "albania", "andorra", "armenia", "azerbaijan", "belarus", "bosnia", "georgia", "kosovo", "moldova", "montenegro", "north_macedonia", "slovenia" -> listOf(FoodTileType.POTATO, FoodTileType.BREAD, FoodTileType.CHICKEN, FoodTileType.EGG, FoodTileType.APPLE, FoodTileType.CHEESE, FoodTileType.TOMATO)
        
        // Middle East / Arab Archetype
        "israel", "jordan", "iran", "iraq", "saudi", "algeria", "tunisia", "libya", "morocco", "cyprus", "malta", "san_marino", "vatican_city", "lebanon", "palestine", "syria" -> listOf(FoodTileType.KEBAB, FoodTileType.FALAFEL, FoodTileType.BREAD, FoodTileType.OLIVES, FoodTileType.TOMATO, FoodTileType.SHAWARMA, FoodTileType.BAKLAVA)
        
        // Africa (General)
        "ghana", "ivory_coast", "senegal", "nigeria", "kenya", "ethiopia", "uganda", "rwanda", "zambia", "zimbabwe", "tanzania", "madagascar", "angola", "benin", "botswana", "burkina_faso", "burundi", "cabo_verde", "cameroon", "central_african_republic", "chad", "comoros", "drc", "republic_congo", "djibouti", "equatorial_guinea", "eritrea", "eswatini", "gabon", "gambia", "guinea", "guinea_bissau", "lesotho", "liberia", "malawi", "mali", "mauritania", "mauritius", "namibia", "niger", "sao_tome", "seychelles", "sierra_leone", "somalia", "south_sudan", "togo", "reunion", "mayotte", "st_helena", "western_sahara" -> listOf(FoodTileType.RICE, FoodTileType.CHICKEN, FoodTileType.FISH, FoodTileType.CORN, FoodTileType.BREAD, FoodTileType.COFFEE)
        
        // South Asian
        "pakistan", "sri_lanka", "bangladesh", "afghanistan", "bhutan", "maldives" -> listOf(FoodTileType.CURRY, FoodTileType.NAAN, FoodTileType.BIRYANI, FoodTileType.RICE, FoodTileType.TEA, FoodTileType.CHICKEN)
        
        // SE Asian
        "indonesia", "philippines", "malaysia", "singapore", "cambodia", "laos", "myanmar", "brunei", "east_timor", "taiwan", "macau" -> listOf(FoodTileType.PAD_THAI, FoodTileType.TOM_YUM, FoodTileType.RICE, FoodTileType.FISH, FoodTileType.SPRING_ROLL, FoodTileType.MANGO_STICKY_RICE)
        
        // Central Asian
        "kazakhstan", "uzbekistan", "mongolia", "kyrgyzstan", "tajikistan", "turkmenistan" -> listOf(FoodTileType.KEBAB, FoodTileType.RICE, FoodTileType.BREAD, FoodTileType.MILK, FoodTileType.STEAK, FoodTileType.POTATO)

        // Middle East / Gulf
        "bahrain", "kuwait", "oman", "qatar", "uae", "yemen" -> listOf(FoodTileType.KEBAB, FoodTileType.FALAFEL, FoodTileType.BREAD, FoodTileType.OLIVES, FoodTileType.SHAWARMA, FoodTileType.TEA)
        
        // South American
        "argentina", "peru", "colombia", "chile", "venezuela", "ecuador", "bolivia", "paraguay", "uruguay", "guyana", "suriname", "falkland_islands", "french_guiana" -> listOf(FoodTileType.STEAK, FoodTileType.CORN, FoodTileType.POTATO, FoodTileType.FISH, FoodTileType.COFFEE, FoodTileType.BREAD)
        
        // Central America / Caribbean
        "bahamas", "jamaica", "dominican_republic", "haiti", "puerto_rico", "panama", "costa_rica", "guatemala", "cuba", "antigua_barbuda", "barbados", "belize", "dominica", "el_salvador", "grenada", "honduras", "nicaragua", "st_kitts_nevis", "st_lucia", "st_vincent", "trinidad_tobago", "bermuda", "cayman_islands", "bvi", "montserrat", "turks_caicos" -> listOf(FoodTileType.BURGER, FoodTileType.FRIES, FoodTileType.STEAK, FoodTileType.FISH, FoodTileType.CORN, FoodTileType.PANCAKE, FoodTileType.DONUT)

        // Oceania
        "fiji", "papua_new_guinea", "samoa", "tonga", "vanuatu" -> listOf(FoodTileType.FISH, FoodTileType.CORN, FoodTileType.MILK, FoodTileType.RICE, FoodTileType.APPLE)

        else -> listOf(FoodTileType.RICE, FoodTileType.CHICKEN, FoodTileType.FISH, FoodTileType.CORN, FoodTileType.BREAD)
    }
}
