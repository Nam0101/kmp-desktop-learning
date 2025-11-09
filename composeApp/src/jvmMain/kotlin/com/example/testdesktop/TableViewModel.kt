package com.example.testdesktop

import androidx.lifecycle.ViewModel
import com.example.testdesktop.data.Medication
import com.example.testdesktop.data.getSampleMedications
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update

enum class SortColumn {
    CODE, NAME, INITIAL_STOCK, IMPORTED, EXPORTED, FINAL_STOCK
}

data class TableUiState(
    val medications: List<Medication> = emptyList(),
    val filteredMedications: List<Medication> = emptyList(),
    val displayedMedications: List<Medication> = emptyList(),
    val searchQuery: String = "",
    val sortColumn: SortColumn? = null,
    val sortAscending: Boolean = true,
    val columnFilters: Map<SortColumn, String> = emptyMap(),
    val currentPage: Int = 1,
    val pageSize: Int = 20,
    val totalPages: Int = 1,
    val totalItems: Int = 0,
    val columnWidths: Map<SortColumn, Float> = mapOf(
        SortColumn.CODE to 180f,
        SortColumn.NAME to 250f,
        SortColumn.INITIAL_STOCK to 120f,
        SortColumn.IMPORTED to 120f,
        SortColumn.EXPORTED to 120f,
        SortColumn.FINAL_STOCK to 120f
    )
)

sealed interface TableUiEvent {
    data class Search(val query: String) : TableUiEvent
    data class SortBy(val column: SortColumn) : TableUiEvent
    data class FilterColumn(val column: SortColumn, val query: String) : TableUiEvent
    data class ClearColumnFilter(val column: SortColumn) : TableUiEvent
    data class GoToPage(val page: Int) : TableUiEvent
    data class ChangePageSize(val pageSize: Int) : TableUiEvent
    data class ResizeColumn(val column: SortColumn, val newWidth: Float) : TableUiEvent
}

class TableViewModel : ViewModel() {
    private val _uiState: MutableStateFlow<TableUiState> = MutableStateFlow(TableUiState())
    val uiState: StateFlow<TableUiState> = _uiState.asStateFlow()

    init {
        loadMedications()
    }

    private fun loadMedications() {
        val medications = getSampleMedications()
        _uiState.update { 
            val totalPages = (medications.size + it.pageSize - 1) / it.pageSize
            it.copy(
                medications = medications,
                filteredMedications = medications,
                displayedMedications = medications.take(it.pageSize),
                totalPages = totalPages,
                totalItems = medications.size,
                currentPage = 1
            )
        }
    }

    fun onEvent(event: TableUiEvent) {
        when (event) {
            is TableUiEvent.Search -> search(event.query)
            is TableUiEvent.SortBy -> sortBy(event.column)
            is TableUiEvent.FilterColumn -> filterColumn(event.column, event.query)
            is TableUiEvent.ClearColumnFilter -> clearColumnFilter(event.column)
            is TableUiEvent.GoToPage -> goToPage(event.page)
            is TableUiEvent.ChangePageSize -> changePageSize(event.pageSize)
            is TableUiEvent.ResizeColumn -> resizeColumn(event.column, event.newWidth)
        }
    }

    private fun resizeColumn(column: SortColumn, newWidth: Float) {
        _uiState.update { currentState ->
            val minWidth = 80f
            val maxWidth = 600f
            val clampedWidth = newWidth.coerceIn(minWidth, maxWidth)
            
            currentState.copy(
                columnWidths = currentState.columnWidths + (column to clampedWidth)
            )
        }
    }

    private fun search(query: String) {
        _uiState.update { currentState ->
            val filtered = applyAllFilters(currentState.medications, query, currentState.columnFilters)
            val sorted = applySorting(filtered, currentState.sortColumn, currentState.sortAscending)
            val totalPages = (sorted.size + currentState.pageSize - 1) / currentState.pageSize
            val page = 1 // Reset to first page on search
            val displayed = sorted.drop((page - 1) * currentState.pageSize).take(currentState.pageSize)
            
            currentState.copy(
                searchQuery = query,
                filteredMedications = sorted,
                displayedMedications = displayed,
                currentPage = page,
                totalPages = totalPages,
                totalItems = sorted.size
            )
        }
    }

    private fun filterColumn(column: SortColumn, query: String) {
        _uiState.update { currentState ->
            val updatedFilters = if (query.isBlank()) {
                currentState.columnFilters - column
            } else {
                currentState.columnFilters + (column to query)
            }
            
            val filtered = applyAllFilters(currentState.medications, currentState.searchQuery, updatedFilters)
            val sorted = applySorting(filtered, currentState.sortColumn, currentState.sortAscending)
            val totalPages = (sorted.size + currentState.pageSize - 1) / currentState.pageSize
            val page = 1 // Reset to first page on filter
            val displayed = sorted.drop((page - 1) * currentState.pageSize).take(currentState.pageSize)
            
            currentState.copy(
                columnFilters = updatedFilters,
                filteredMedications = sorted,
                displayedMedications = displayed,
                currentPage = page,
                totalPages = totalPages,
                totalItems = sorted.size
            )
        }
    }

    private fun clearColumnFilter(column: SortColumn) {
        filterColumn(column, "")
    }

    private fun applyAllFilters(
        medications: List<Medication>,
        globalSearch: String,
        columnFilters: Map<SortColumn, String>
    ): List<Medication> {
        var filtered = medications

        // Apply global search
        if (globalSearch.isNotBlank()) {
            filtered = filtered.filter { medication ->
                medication.code.contains(globalSearch, ignoreCase = true) ||
                medication.name.contains(globalSearch, ignoreCase = true)
            }
        }

        // Apply column-specific filters
        columnFilters.forEach { (column, query) ->
            if (query.isNotBlank()) {
                filtered = filtered.filter { medication ->
                    when (column) {
                        SortColumn.CODE -> medication.code.contains(query, ignoreCase = true)
                        SortColumn.NAME -> medication.name.contains(query, ignoreCase = true)
                        else -> true // Numeric columns don't have text filters
                    }
                }
            }
        }

        return filtered
    }

    private fun sortBy(column: SortColumn) {
        _uiState.update { currentState ->
            val ascending = if (currentState.sortColumn == column) {
                !currentState.sortAscending
            } else {
                true
            }
            
            val sorted = applySorting(currentState.filteredMedications, column, ascending)
            val displayed = sorted.drop((currentState.currentPage - 1) * currentState.pageSize).take(currentState.pageSize)
            
            currentState.copy(
                sortColumn = column,
                sortAscending = ascending,
                filteredMedications = sorted,
                displayedMedications = displayed
            )
        }
    }

    private fun goToPage(page: Int) {
        _uiState.update { currentState ->
            if (page < 1 || page > currentState.totalPages) return@update currentState
            
            val displayed = currentState.filteredMedications
                .drop((page - 1) * currentState.pageSize)
                .take(currentState.pageSize)
            
            currentState.copy(
                currentPage = page,
                displayedMedications = displayed
            )
        }
    }

    private fun changePageSize(pageSize: Int) {
        _uiState.update { currentState ->
            val totalPages = (currentState.filteredMedications.size + pageSize - 1) / pageSize
            val page = 1 // Reset to first page
            val displayed = currentState.filteredMedications.take(pageSize)
            
            currentState.copy(
                pageSize = pageSize,
                currentPage = page,
                totalPages = totalPages,
                displayedMedications = displayed
            )
        }
    }

    private fun applySorting(
        medications: List<Medication>,
        column: SortColumn?,
        ascending: Boolean
    ): List<Medication> {
        if (column == null) return medications

        val sorted = when (column) {
            SortColumn.CODE -> medications.sortedBy { it.code }
            SortColumn.NAME -> medications.sortedBy { it.name }
            SortColumn.INITIAL_STOCK -> medications.sortedBy { it.initialStock }
            SortColumn.IMPORTED -> medications.sortedBy { it.imported }
            SortColumn.EXPORTED -> medications.sortedBy { it.exported }
            SortColumn.FINAL_STOCK -> medications.sortedBy { it.finalStock }
        }

        return if (ascending) sorted else sorted.reversed()
    }
}

