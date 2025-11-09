package com.example.testdesktop.data

data class Medication(
    val code: String,           // Mã Góc
    val name: String,           // Tên Thuốc Góc
    val initialStock: Int,      // Tồn đầu
    val imported: Int,          // Nhập
    val exported: Int,          // Xuất
    val finalStock: Int         // Tồn cuối
)

// Sample data
fun getSampleMedications(): List<Medication> {
    val medicationNames = listOf(
        "Amoxicilin", "Paracetamol", "Vitamin C", "Aspirin", "Ibuprofen",
        "Omeprazole", "Metformin", "Atorvastatin", "Losartan", "Amlodipine",
        "Ciprofloxacin", "Azithromycin", "Cephalexin", "Doxycycline", "Clindamycin",
        "Pantoprazole", "Esomeprazole", "Lansoprazole", "Rabeprazole", "Famotidine",
        "Lisinopril", "Enalapril", "Ramipril", "Perindopril", "Candesartan",
        "Simvastatin", "Pravastatin", "Rosuvastatin", "Fluvastatin", "Lovastatin",
        "Glipizide", "Glyburide", "Glimepiride", "Pioglitazone", "Sitagliptin",
        "Levothyroxine", "Liothyronine", "Methimazole", "Propylthiouracil", "Carbimazole",
        "Furosemide", "Hydrochlorothiazide", "Spironolactone", "Amiloride", "Triamterene",
        "Cetirizine", "Loratadine", "Fexofenadine", "Diphenhydramine", "Chlorpheniramine"
    )
    
    val dosages = listOf("5mg", "10mg", "20mg", "25mg", "50mg", "100mg", "250mg", "500mg", "650mg", "1000mg")
    val prefixes = listOf("VD", "VN", "VT", "VM", "VH")
    val years = listOf("20", "21", "22", "23")
    
    return (1..100).map { index ->
        val prefix = prefixes[index % prefixes.size]
        val medicationName = medicationNames[index % medicationNames.size]
        val dosage = dosages[index % dosages.size]
        val year = years[index % years.size]
        
        val code = "$prefix-${(10000 + index).toString().padStart(5, '0')}-$year"
        val name = "$medicationName $dosage"
        
        // Generate realistic random numbers
        val initialStock = (50..500).random()
        val imported = (100..1000).random()
        val exported = (initialStock + imported * 0.6).toInt() + (-50..50).random()
        val finalStock = initialStock + imported - exported
        
        Medication(
            code = code,
            name = name,
            initialStock = initialStock,
            imported = imported,
            exported = exported,
            finalStock = finalStock
        )
    }
}

