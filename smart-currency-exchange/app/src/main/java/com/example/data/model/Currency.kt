package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "currencies")
data class Currency(
    @PrimaryKey val code: String, // e.g. "USD", must be uppercase and unique
    val name: String,             // e.g. "US Dollar"
    val symbol: String,           // e.g. "$"
    val value: Double,            // Exchange value relative to the base reference
    val notes: String = "",       // Optional custom notes
    val isFavorite: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val offlineValue: Double? = null
)

val Currency.offlineValueOrValue: Double
    get() = offlineValue ?: value

val Currency.isCrypto: Boolean
    get() {
        val c = code.uppercase(java.util.Locale.US).trim()
        val n = name.uppercase(java.util.Locale.US).trim()
        val cryptoSet = setOf(
            "BTC", "TON", "ETH", "SOL", "BNB", "ADA", "DOGE", "XRP", "USDT", "USDC", "LTC", "LINK", "DOT", "POL", "SHIB", "AVAX",
            "TRX", "NEAR", "UNI", "ATOM", "ALGO", "FTM", "OP", "ARB", "LDO", "ICP", "VET", "FIL", "ETC", "HBAR", "GRT", "RNDR",
            "THETA", "MKR", "STX", "EGLD", "RUNE", "INJ", "SUI", "SEI", "APT", "TIA", "MINA", "IMX", "GALA", "AAVE", "FLOW", "SAND", "MANA", "AXS", "EOS", "XTZ", "IOTA", "BCH", "XLM", "XMR", "QNT", "MKR", "LRC", "BAT", "WAVES", "ZEC", "DASH", "OMG", "ZIL", "ENJ", "KNC", "CRV", "COMP", "SNX", "YFI", "CAKE", "RPL", "WOO", "CHZ", "HOT", "ONE", "ANKR", "CELO", "NANO", "RVN", "XEC", "TUSD", "USDP", "FDUSD", "BUSD", "GUSD", "PYUSD"
        )
        return cryptoSet.contains(c) || n.contains("COIN") || n.contains("TOKEN") || n.contains("CRYPTOCURRENCY")
    }

val Currency.isCommodity: Boolean
    get() {
        val c = code.uppercase(java.util.Locale.US).trim()
        val n = name.uppercase(java.util.Locale.US).trim()
        val commoditySet = setOf(
            "GOLD", "SILVER", "DIAMOND", "PLATINUM", "GOLD_24K", "GOLD_18K", "BRONZE", "COPPER", "PALLADIUM", 
            "XAU", "XAG", "XPT", "XPD"
        )
        return commoditySet.contains(c) || n.contains("GOLD") || n.contains("SILVER") || n.contains("PLATINUM") || n.contains("DIAMOND")
    }
