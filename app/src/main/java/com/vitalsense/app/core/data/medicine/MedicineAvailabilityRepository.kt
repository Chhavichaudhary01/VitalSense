package com.vitalsense.app.core.data.medicine

/**
 * Repository interface for querying nearby pharmacy availability and clinically interchangeable alternatives.
 */
interface MedicineAvailabilityRepository {

    /**
     * Retrieves nearby pharmacy locations within [radiusMeters] and assigns stock status
     * for [medicineId] based on store chain patterns or deterministic fallback.
     */
    suspend fun getNearbyStoresWithAvailability(
        medicineId: String,
        patientLat: Double,
        patientLng: Double,
        radiusMeters: Int = 5000
    ): List<StoreAvailabilityResult>

    /**
     * Suggests clinically relevant alternatives for [medicineId] belonging strictly to the
     * same therapeutic class.
     */
    fun suggestAlternatives(
        medicineId: String,
        unavailableAt: List<StoreAvailabilityResult> = emptyList()
    ): List<Medicine>

    /**
     * Finds a medicine definition by its unique identifier.
     */
    fun getMedicineById(medicineId: String): Medicine?

    /**
     * Finds a medicine definition by approximate name matching.
     */
    fun findMedicineByName(name: String): Medicine?

    /**
     * Returns the full reference catalog.
     */
    fun getAllMedicines(): List<Medicine>

    /**
     * Pure function to determine whether a medicine is in stock at a given store.
     * Returns Pair(inStock: Boolean, isKnownChain: Boolean).
     */
    fun calculateStockStatus(placeId: String, storeName: String, medicineId: String): Pair<Boolean, Boolean>
}
