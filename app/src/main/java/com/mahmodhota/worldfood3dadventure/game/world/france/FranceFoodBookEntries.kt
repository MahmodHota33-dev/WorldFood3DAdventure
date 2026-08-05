package com.mahmodhota.worldfood3dadventure.game.world.france

/**
 * Metadata for French dishes in the Food Book.
 */
data class FoodBookEntry(
    val id: String,
    val name: String,
    val country: String = "France",
    val description: String,
    val unlocked: Boolean = false
)

object FranceFoodBookEntries {
    val entries = listOf(
        FoodBookEntry("fr_croissant", "Croissant", description = "A buttery, flaky, viennoiserie pastry named for its historical crescent shape."),
        FoodBookEntry("fr_baguette", "Baguette", description = "A long, thin loaf of French bread that is commonly made from basic lean dough."),
        FoodBookEntry("fr_camembert", "Camembert", description = "A soft, creamy French cheese with a rich and earthy finish."),
        FoodBookEntry("fr_creme_brulee", "Crème Brûlée", description = "A silky custard dessert with a crisp caramelized sugar crust."),
        FoodBookEntry("fr_cheese", "French Cheese", description = "France is home to over 1,600 distinct types of cheese, including Brie and Camembert."),
        FoodBookEntry("fr_crepe", "Crêpe", description = "A very thin pancake, usually made from wheat flour or buckwheat flour."),
        FoodBookEntry("fr_macaron", "Macaron", description = "A sweet meringue-based confection made with egg white, icing sugar, and almond meal."),
        FoodBookEntry("fr_ratatouille", "Ratatouille", description = "A traditional French stewed vegetable dish, originating in Nice."),
        FoodBookEntry("fr_eclair", "Éclair", description = "An oblong pastry made with choux dough filled with a cream and topped with icing."),
        FoodBookEntry("fr_souffle", "Soufflé", description = "A baked egg-based dish which originated in early 18th century France."),
        FoodBookEntry("fr_tarte_tatin", "Tarte Tatin", description = "An upside-down tart in which the fruit are caramelized in butter and sugar before the tart is baked."),
        FoodBookEntry("fr_wine", "French Wine", description = "A decorative symbol of French dining culture and vineyard heritage."),
        FoodBookEntry("fr_master", "France Master", description = "A collection of the finest culinary achievements in French cuisine.")
    )
}
