package com.example.testdesktop

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.hoverable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.testdesktop.data.Medication
import java.awt.Cursor

@Composable
fun TableScreen(
    modifier: Modifier = Modifier
) {
    val viewModel: TableViewModel = remember { TableViewModel() }
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp)
    ) {
        Text(
            text = "Quản Lý Thuốc",
            style = MaterialTheme.typography.headlineLarge,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Search bar
        OutlinedTextField(
            value = uiState.searchQuery,
            onValueChange = { viewModel.onEvent(TableUiEvent.Search(it)) },
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp),
            placeholder = { Text("Tìm kiếm theo mã hoặc tên thuốc...") },
            leadingIcon = {
                Icon(Icons.Default.Search, contentDescription = "Search")
            },
            singleLine = true
        )

        // Info bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Hiển thị ${uiState.displayedMedications.size} / ${uiState.totalItems} thuốc",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            // Page size selector
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Hiển thị:",
                    style = MaterialTheme.typography.bodyMedium
                )
                listOf(10, 20, 50, 100).forEach { size ->
                    FilterChip(
                        selected = uiState.pageSize == size,
                        onClick = { viewModel.onEvent(TableUiEvent.ChangePageSize(size)) },
                        label = { Text("$size") }
                    )
                }
            }
        }

        // Table
        Card(
            modifier = Modifier.weight(1f),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column {
                // Header
                TableHeader(
                    sortColumn = uiState.sortColumn,
                    sortAscending = uiState.sortAscending,
                    columnFilters = uiState.columnFilters,
                    columnWidths = uiState.columnWidths,
                    onSort = { column -> viewModel.onEvent(TableUiEvent.SortBy(column)) },
                    onFilterColumn = { column, query -> viewModel.onEvent(TableUiEvent.FilterColumn(column, query)) },
                    onClearFilter = { column -> viewModel.onEvent(TableUiEvent.ClearColumnFilter(column)) },
                    onResizeColumn = { column, width -> viewModel.onEvent(TableUiEvent.ResizeColumn(column, width)) }
                )

                Divider(thickness = 2.dp)

                // Body
                LazyColumn(
                    modifier = Modifier.weight(1f)
                ) {
                    itemsIndexed(uiState.displayedMedications) { index, medication ->
                        TableRow(
                            medication = medication,
                            isEven = index % 2 == 0,
                            columnWidths = uiState.columnWidths
                        )
                        if (index < uiState.displayedMedications.size - 1) {
                            Divider(thickness = 0.5.dp)
                        }
                    }
                }
                
                Divider(thickness = 2.dp)
                
                // Pagination
                PaginationBar(
                    currentPage = uiState.currentPage,
                    totalPages = uiState.totalPages,
                    onPageChange = { page -> viewModel.onEvent(TableUiEvent.GoToPage(page)) }
                )
            }
        }
    }
}

@Composable
private fun TableHeader(
    sortColumn: SortColumn?,
    sortAscending: Boolean,
    columnFilters: Map<SortColumn, String>,
    columnWidths: Map<SortColumn, Float>,
    onSort: (SortColumn) -> Unit,
    onFilterColumn: (SortColumn, String) -> Unit,
    onClearFilter: (SortColumn) -> Unit,
    onResizeColumn: (SortColumn, Float) -> Unit
) {
    // Calculate if we need to auto-fill
    val defaultWidths = mapOf(
        SortColumn.CODE to 180f,
        SortColumn.NAME to 250f,
        SortColumn.INITIAL_STOCK to 120f,
        SortColumn.IMPORTED to 120f,
        SortColumn.EXPORTED to 120f,
        SortColumn.FINAL_STOCK to 120f
    )
    val hasCustomWidth = columnWidths.any { (col, width) -> 
        width != defaultWidths[col]
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant)
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!hasCustomWidth) {
            // Auto-fill mode: use weights
            ResizableHeaderCell(
                text = "Mã Góc",
                column = SortColumn.CODE,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                filterValue = columnFilters[SortColumn.CODE] ?: "",
                width = null,
                weight = 0.75f,
                onSort = onSort,
                onFilter = onFilterColumn,
                onClearFilter = onClearFilter,
                onResize = onResizeColumn
            )
            
            ResizableHeaderCell(
                text = "Tên Thuốc Góc",
                column = SortColumn.NAME,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                filterValue = columnFilters[SortColumn.NAME] ?: "",
                width = null,
                weight = 2.5f,
                onSort = onSort,
                onFilter = onFilterColumn,
                onClearFilter = onClearFilter,
                onResize = onResizeColumn
            )
            
            ResizableNumericHeaderCell(
                text = "Tồn đầu",
                column = SortColumn.INITIAL_STOCK,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                width = null,
                weight = 1.2f,
                onSort = onSort,
                onResize = onResizeColumn
            )
            
            ResizableNumericHeaderCell(
                text = "Nhập",
                column = SortColumn.IMPORTED,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                width = null,
                weight = 1.2f,
                onSort = onSort,
                onResize = onResizeColumn
            )
            
            ResizableNumericHeaderCell(
                text = "Xuất",
                column = SortColumn.EXPORTED,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                width = null,
                weight = 1.2f,
                onSort = onSort,
                onResize = onResizeColumn
            )
            
            ResizableNumericHeaderCell(
                text = "Tồn cuối",
                column = SortColumn.FINAL_STOCK,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                width = null,
                weight = 1.2f,
                onSort = onSort,
                onResize = onResizeColumn,
                isLast = true
            )
        } else {
            // Custom width mode: use fixed widths
            ResizableHeaderCell(
                text = "Mã Góc",
                column = SortColumn.CODE,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                filterValue = columnFilters[SortColumn.CODE] ?: "",
                width = columnWidths[SortColumn.CODE] ?: 180f,
                weight = null,
                onSort = onSort,
                onFilter = onFilterColumn,
                onClearFilter = onClearFilter,
                onResize = onResizeColumn
            )
            
            ResizableHeaderCell(
                text = "Tên Thuốc Góc",
                column = SortColumn.NAME,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                filterValue = columnFilters[SortColumn.NAME] ?: "",
                width = columnWidths[SortColumn.NAME] ?: 250f,
                weight = null,
                onSort = onSort,
                onFilter = onFilterColumn,
                onClearFilter = onClearFilter,
                onResize = onResizeColumn
            )
            
            ResizableNumericHeaderCell(
                text = "Tồn đầu",
                column = SortColumn.INITIAL_STOCK,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                width = columnWidths[SortColumn.INITIAL_STOCK] ?: 120f,
                weight = null,
                onSort = onSort,
                onResize = onResizeColumn
            )
            
            ResizableNumericHeaderCell(
                text = "Nhập",
                column = SortColumn.IMPORTED,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                width = columnWidths[SortColumn.IMPORTED] ?: 120f,
                weight = null,
                onSort = onSort,
                onResize = onResizeColumn
            )
            
            ResizableNumericHeaderCell(
                text = "Xuất",
                column = SortColumn.EXPORTED,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                width = columnWidths[SortColumn.EXPORTED] ?: 120f,
                weight = null,
                onSort = onSort,
                onResize = onResizeColumn
            )
            
            ResizableNumericHeaderCell(
                text = "Tồn cuối",
                column = SortColumn.FINAL_STOCK,
                sortColumn = sortColumn,
                sortAscending = sortAscending,
                width = columnWidths[SortColumn.FINAL_STOCK] ?: 120f,
                weight = null,
                onSort = onSort,
                onResize = onResizeColumn,
                isLast = true
            )
        }
    }
}

@Composable
private fun FilterableHeaderCell(
    text: String,
    column: SortColumn,
    sortColumn: SortColumn?,
    sortAscending: Boolean,
    filterValue: String,
    onSort: (SortColumn) -> Unit,
    onFilter: (SortColumn, String) -> Unit,
    onClearFilter: (SortColumn) -> Unit,
    modifier: Modifier = Modifier
) {
    var showFilterMenu by remember { mutableStateOf(false) }
    var filterText by remember(filterValue) { mutableStateOf(filterValue) }
    val isActive = sortColumn == column
    val hasFilter = filterValue.isNotBlank()

    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Header text with filter icon
            Row(
                modifier = Modifier
                    .weight(1f)
                    .clickable { showFilterMenu = true },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = when {
                        hasFilter -> MaterialTheme.colorScheme.tertiary
                        isActive -> MaterialTheme.colorScheme.primary
                        else -> MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
                
                if (hasFilter) {
                    Icon(
                        imageVector = Icons.Default.FilterList,
                        contentDescription = "Filtered",
                        tint = MaterialTheme.colorScheme.tertiary,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            // Sort icon (clickable)
            if (isActive) {
                IconButton(
                    onClick = { onSort(column) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (sortAscending) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = if (sortAscending) "Ascending" else "Descending",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else {
                IconButton(
                    onClick = { onSort(column) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowDropDown,
                        contentDescription = "Sort",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        // Filter dropdown menu
        DropdownMenu(
            expanded = showFilterMenu,
            onDismissRequest = { showFilterMenu = false }
        ) {
            Column(
                modifier = Modifier
                    .width(250.dp)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "Lọc theo $text",
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold
                )

                OutlinedTextField(
                    value = filterText,
                    onValueChange = { filterText = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Nhập từ khóa...") },
                    singleLine = true,
                    trailingIcon = {
                        if (filterText.isNotEmpty()) {
                            IconButton(onClick = { filterText = "" }) {
                                Icon(Icons.Default.Close, contentDescription = "Clear")
                            }
                        }
                    }
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedButton(
                        onClick = {
                            filterText = ""
                            onClearFilter(column)
                            showFilterMenu = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Xóa")
                    }

                    Button(
                        onClick = {
                            onFilter(column, filterText)
                            showFilterMenu = false
                        },
                        modifier = Modifier.weight(1f)
                    ) {
                        Text("Áp dụng")
                    }
                }
            }
        }
    }
}

@Composable
private fun SortableHeaderCell(
    text: String,
    column: SortColumn,
    sortColumn: SortColumn?,
    sortAscending: Boolean,
    onSort: (SortColumn) -> Unit,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Start
) {
    val isActive = sortColumn == column
    
    Row(
        modifier = modifier
            .clickable { onSort(column) }
            .padding(horizontal = 8.dp, vertical = 4.dp),
        horizontalArrangement = when (align) {
            TextAlign.End -> Arrangement.End
            TextAlign.Center -> Arrangement.Center
            else -> Arrangement.Start
        },
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
        )
        
        if (isActive) {
            Icon(
                imageVector = if (sortAscending) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                contentDescription = if (sortAscending) "Ascending" else "Descending",
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun ResizableHeaderCell(
    text: String,
    column: SortColumn,
    sortColumn: SortColumn?,
    sortAscending: Boolean,
    filterValue: String,
    width: Float?,
    weight: Float?,
    onSort: (SortColumn) -> Unit,
    onFilter: (SortColumn, String) -> Unit,
    onClearFilter: (SortColumn) -> Unit,
    onResize: (SortColumn, Float) -> Unit
) {
    val density = LocalDensity.current
    var currentWidth by remember(width) { mutableStateOf(width) }
    var showFilterMenu by remember { mutableStateOf(false) }
    var filterText by remember(filterValue) { mutableStateOf(filterValue) }
    val isActive = sortColumn == column
    val hasFilter = filterValue.isNotBlank()

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = if (weight != null) {
                Modifier.weight(weight)
            } else {
                Modifier.width(with(density) { (currentWidth ?: 180f).dp })
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .clickable { showFilterMenu = true },
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Text(
                        text = text,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.Bold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        color = when {
                            hasFilter -> MaterialTheme.colorScheme.tertiary
                            isActive -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.onSurfaceVariant
                        }
                    )
                    if (hasFilter) {
                        Icon(
                            imageVector = Icons.Default.FilterList,
                            contentDescription = "Filtered",
                            tint = MaterialTheme.colorScheme.tertiary,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                IconButton(
                    onClick = { onSort(column) },
                    modifier = Modifier.size(24.dp)
                ) {
                    Icon(
                        imageVector = if (isActive && sortAscending) Icons.Default.ArrowDropUp 
                                     else Icons.Default.ArrowDropDown,
                        contentDescription = "Sort",
                        tint = if (isActive) MaterialTheme.colorScheme.primary 
                              else MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.3f),
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            DropdownMenu(
                expanded = showFilterMenu,
                onDismissRequest = { showFilterMenu = false }
            ) {
                Column(
                    modifier = Modifier.width(250.dp).padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(text = "Lọc theo $text", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    OutlinedTextField(
                        value = filterText,
                        onValueChange = { filterText = it },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text("Nhập từ khóa...") },
                        singleLine = true,
                        trailingIcon = {
                            if (filterText.isNotEmpty()) {
                                IconButton(onClick = { filterText = "" }) {
                                    Icon(Icons.Default.Close, contentDescription = "Clear")
                                }
                            }
                        }
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                filterText = ""
                                onClearFilter(column)
                                showFilterMenu = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Xóa")
                        }
                        Button(
                            onClick = {
                                onFilter(column, filterText)
                                showFilterMenu = false
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Áp dụng")
                        }
                    }
                }
            }
        }

        ResizeDivider(
            onResize = { delta ->
                currentWidth = ((currentWidth ?: 180f) + delta).coerceIn(80f, 600f)
                onResize(column, currentWidth!!)
            }
        )
    }
}

@Composable
private fun ResizableNumericHeaderCell(
    text: String,
    column: SortColumn,
    sortColumn: SortColumn?,
    sortAscending: Boolean,
    width: Float?,
    weight: Float?,
    onSort: (SortColumn) -> Unit,
    onResize: (SortColumn, Float) -> Unit,
    isLast: Boolean = false
) {
    val density = LocalDensity.current
    var currentWidth by remember(width) { mutableStateOf(width) }
    val isActive = sortColumn == column

    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = if (weight != null) {
                Modifier.weight(weight)
            } else {
                Modifier.width(with(density) { (currentWidth ?: 120f).dp })
            }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSort(column) }
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.Bold,
                    color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (isActive) {
                    Icon(
                        imageVector = if (sortAscending) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                        contentDescription = if (sortAscending) "Ascending" else "Descending",
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        if (!isLast) {
            ResizeDivider(
                onResize = { delta ->
                    currentWidth = ((currentWidth ?: 120f) + delta).coerceIn(80f, 600f)
                    onResize(column, currentWidth!!)
                }
            )
        }
    }
}

@Composable
private fun ResizeDivider(
    onResize: (Float) -> Unit
) {
    var isDragging by remember { mutableStateOf(false) }
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()

    Box(
        modifier = Modifier
            .width(8.dp)
            .fillMaxHeight()
            .hoverable(interactionSource)
            .pointerHoverIcon(PointerIcon(Cursor(Cursor.E_RESIZE_CURSOR)))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { isDragging = true },
                    onDragEnd = { isDragging = false },
                    onDragCancel = { isDragging = false }
                ) { change, dragAmount ->
                    change.consume()
                    onResize(dragAmount.x)
                }
            }
    ) {
        Box(
            modifier = Modifier
                .width(if (isDragging || isHovered) 2.dp else 1.dp)
                .fillMaxHeight()
                .align(Alignment.Center)
                .background(
                    if (isDragging) MaterialTheme.colorScheme.primary
                    else if (isHovered) MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    else MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)
                )
        )
    }
}

@Composable
private fun TableRow(
    medication: Medication,
    isEven: Boolean,
    columnWidths: Map<SortColumn, Float>
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    
    val backgroundColor = when {
        isHovered -> MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f)
        isEven -> MaterialTheme.colorScheme.surface
        else -> MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    }
    
    val density = LocalDensity.current
    
    // Check if using custom widths
    val defaultWidths = mapOf(
        SortColumn.CODE to 180f,
        SortColumn.NAME to 250f,
        SortColumn.INITIAL_STOCK to 120f,
        SortColumn.IMPORTED to 120f,
        SortColumn.EXPORTED to 120f,
        SortColumn.FINAL_STOCK to 120f
    )
    val hasCustomWidth = columnWidths.any { (col, width) -> 
        width != defaultWidths[col]
    }
    
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(backgroundColor)
            .hoverable(interactionSource)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        if (!hasCustomWidth) {
            // Auto-fill mode: use weights
            TableCell(
                text = medication.code,
                modifier = Modifier.weight(1.5f),
                align = TextAlign.Start
            )
            
            TableCell(
                text = medication.name,
                modifier = Modifier.weight(2.5f),
                align = TextAlign.Start
            )
            
            TableCell(
                text = medication.initialStock.toString(),
                modifier = Modifier.weight(1.2f),
                align = TextAlign.End
            )
            
            TableCell(
                text = medication.imported.toString(),
                modifier = Modifier.weight(1.2f),
                align = TextAlign.End,
                color = MaterialTheme.colorScheme.primary
            )
            
            TableCell(
                text = medication.exported.toString(),
                modifier = Modifier.weight(1.2f),
                align = TextAlign.End,
                color = MaterialTheme.colorScheme.error
            )
            
            TableCell(
                text = medication.finalStock.toString(),
                modifier = Modifier.weight(1.2f),
                align = TextAlign.End,
                fontWeight = FontWeight.Bold,
                color = if (medication.finalStock < 100) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.tertiary
            )
        } else {
            // Custom width mode: use fixed widths
            TableCell(
                text = medication.code,
                modifier = Modifier.width(with(density) { (columnWidths[SortColumn.CODE] ?: 180f).dp }),
                align = TextAlign.Start
            )
            Spacer(Modifier.width(8.dp))
            
            TableCell(
                text = medication.name,
                modifier = Modifier.width(with(density) { (columnWidths[SortColumn.NAME] ?: 250f).dp }),
                align = TextAlign.Start
            )
            Spacer(Modifier.width(8.dp))
            
            TableCell(
                text = medication.initialStock.toString(),
                modifier = Modifier.width(with(density) { (columnWidths[SortColumn.INITIAL_STOCK] ?: 120f).dp }),
                align = TextAlign.End
            )
            Spacer(Modifier.width(8.dp))
            
            TableCell(
                text = medication.imported.toString(),
                modifier = Modifier.width(with(density) { (columnWidths[SortColumn.IMPORTED] ?: 120f).dp }),
                align = TextAlign.End,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(Modifier.width(8.dp))
            
            TableCell(
                text = medication.exported.toString(),
                modifier = Modifier.width(with(density) { (columnWidths[SortColumn.EXPORTED] ?: 120f).dp }),
                align = TextAlign.End,
                color = MaterialTheme.colorScheme.error
            )
            Spacer(Modifier.width(8.dp))
            
            TableCell(
                text = medication.finalStock.toString(),
                modifier = Modifier.width(with(density) { (columnWidths[SortColumn.FINAL_STOCK] ?: 120f).dp }),
                align = TextAlign.End,
                fontWeight = FontWeight.Bold,
                color = if (medication.finalStock < 100) 
                    MaterialTheme.colorScheme.error 
                else 
                    MaterialTheme.colorScheme.tertiary
            )
        }
    }
}

@Composable
private fun TableCell(
    text: String,
    modifier: Modifier = Modifier,
    align: TextAlign = TextAlign.Start,
    fontWeight: FontWeight = FontWeight.Normal,
    color: Color = Color.Unspecified
) {
    Text(
        text = text,
        modifier = modifier.padding(horizontal = 8.dp),
        style = MaterialTheme.typography.bodyMedium,
        textAlign = align,
        fontWeight = fontWeight,
        color = color,
        maxLines = 1,
        overflow = TextOverflow.Ellipsis
    )
}

@Composable
private fun PaginationBar(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface),
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Previous button
            IconButton(
                onClick = { onPageChange(currentPage - 1) },
                enabled = currentPage > 1
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Page"
                )
            }

            Spacer(Modifier.width(16.dp))

            // Page numbers
            val pageRange = getPageRange(currentPage, totalPages)
            
            pageRange.forEach { page ->
                when (page) {
                    -1 -> {
                        // Ellipsis
                        Text(
                            text = "...",
                            modifier = Modifier.padding(horizontal = 8.dp),
                            style = MaterialTheme.typography.bodyMedium
                        )
                    }
                    else -> {
                        // Page number button
                        val isCurrentPage = page == currentPage
                        Surface(
                            modifier = Modifier
                                .size(40.dp)
                                .clickable { onPageChange(page) },
                            shape = CircleShape,
                            color = if (isCurrentPage) 
                                MaterialTheme.colorScheme.primary 
                            else 
                                Color.Transparent,
                            border = if (!isCurrentPage) 
                                BorderStroke(1.dp, MaterialTheme.colorScheme.outline) 
                            else 
                                null
                        ) {
                            Box(
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = page.toString(),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = if (isCurrentPage) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrentPage) 
                                        MaterialTheme.colorScheme.onPrimary 
                                    else 
                                        MaterialTheme.colorScheme.onSurface
                                )
                            }
                        }
                        
                        Spacer(Modifier.width(8.dp))
                    }
                }
            }

            Spacer(Modifier.width(8.dp))

            // Next button
            IconButton(
                onClick = { onPageChange(currentPage + 1) },
                enabled = currentPage < totalPages
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Page"
                )
            }

            Spacer(Modifier.width(16.dp))

            // Page info
            Text(
                text = "Trang $currentPage / $totalPages",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun getPageRange(currentPage: Int, totalPages: Int): List<Int> {
    if (totalPages <= 7) {
        return (1..totalPages).toList()
    }

    val result = mutableListOf<Int>()
    
    // Always show first page
    result.add(1)
    
    when {
        currentPage <= 4 -> {
            // Show first 5 pages
            result.addAll(2..5)
            result.add(-1) // Ellipsis
            result.add(totalPages)
        }
        currentPage >= totalPages - 3 -> {
            // Show last 5 pages
            result.add(-1) // Ellipsis
            result.addAll((totalPages - 4)..totalPages)
        }
        else -> {
            // Show current page and neighbors
            result.add(-1) // Ellipsis
            result.addAll((currentPage - 1)..(currentPage + 1))
            result.add(-1) // Ellipsis
            result.add(totalPages)
        }
    }
    
    return result
}

