# TestDesktop - KMP Desktop Learning Project

## 📝 Giới thiệu

Đây là một **test repository** để học và đánh giá tính khả thi của **Kotlin Multiplatform (KMP) Desktop** với Compose Multiplatform.

Project này được tạo ra nhằm mục đích:
- ✅ Học và thực hành Compose for Desktop
- ✅ Đánh giá tính khả thi của KMP Desktop cho ứng dụng thực tế
- ✅ Thử nghiệm các UI components và patterns
- ✅ Test performance và khả năng mở rộng

## 🚀 Tính năng đã thực hiện

### 📊 Data Table Component
- **Pagination**: Phân trang với nhiều lựa chọn page size (10, 20, 50, 100)
- **Sorting**: Sort theo bất kỳ cột nào (ascending/descending)
- **Column Filtering**: Filter riêng cho từng cột text
- **Global Search**: Tìm kiếm toàn bộ bảng
- **Resizable Columns**: Kéo để thay đổi độ rộng cột
- **Auto-fill Width**: Tự động fill đủ màn hình khi chưa resize
- **Striped Rows**: Dòng chẵn/lẻ màu khác nhau
- **Hover Effects**: Highlight khi di chuột
- **Color Coding**: Màu sắc semantic cho import/export/stock levels

### 🎨 Navigation
- Sidebar có thể ẩn/hiện
- Multiple screens (Home, Table, Profile, Settings, About)
- Top bar với navigation controls
- Material Design 3

### 📦 Sample Data
- 100+ fake medications data
- Realistic numbers cho testing

## 🛠️ Tech Stack

- **Kotlin**: 2.2.20
- **Compose Multiplatform**: 1.9.1
- **Target Platform**: JVM Desktop
- **UI**: Material3 Design
- **Architecture**: MVVM with ViewModels
- **State Management**: Kotlin Flow

## 🏃‍♂️ Chạy ứng dụng

### Development mode
```bash
./gradlew :composeApp:run
```

### Build distribution
```bash
./gradlew :composeApp:packageDistributionForCurrentOS
```

File output: `composeApp/build/compose/binaries/main/distribution/`

### Build JAR file
```bash
./gradlew :composeApp:packageUberJarForCurrentOS
```

## 📁 Cấu trúc dự án

```
TestDesktop/
├── composeApp/
│   └── src/jvmMain/kotlin/com/example/testdesktop/
│       ├── data/
│       │   └── Medication.kt          # Data models
│       ├── App.kt                      # Main app entry
│       ├── AppViewModel.kt             # Main navigation state
│       ├── Sidebar.kt                  # Sidebar navigation
│       ├── Screens.kt                  # Home, Profile, Settings, About
│       ├── TableScreen.kt              # Advanced data table (800+ lines)
│       ├── TableViewModel.kt           # Table state management
│       └── main.kt                     # Application entry point
└── gradle/
    └── libs.versions.toml              # Dependencies
```

## 🎯 Kết luận về tính khả thi

### ✅ Ưu điểm
1. **Performance tốt**: UI mượt mà, không lag ngay cả với 100+ rows
2. **Easy to build**: Setup và build khá đơn giản
3. **Native look**: Ứng dụng trông native trên desktop
4. **Code sharing**: Có thể share logic giữa các platforms
5. **Modern UI**: Material3 components đẹp và đầy đủ
6. **Type-safe**: Kotlin's type system giúp tránh bugs

### ⚠️ Hạn chế
1. **Bundle size**: File output khá lớn (>100MB)
2. **Startup time**: Khởi động hơi chậm so với native apps
3. **Memory usage**: JVM-based nên tiêu tốn RAM
4. **Platform-specific features**: Một số tính năng cần native code
5. **Documentation**: Tài liệu chưa nhiều như React/Electron

### 💡 Đánh giá chung
KMP Desktop với Compose **hoàn toàn khả thi** cho:
- ✅ Internal tools / Admin panels
- ✅ Business applications
- ✅ Data management tools
- ✅ Cross-platform apps cần code sharing

Chưa phù hợp cho:
- ❌ Apps cần file size nhỏ
- ❌ Apps cần startup time cực nhanh
- ❌ Apps phụ thuộc nhiều vào platform-specific APIs

## 📚 Học được gì

1. **Compose for Desktop** khá giống Compose for Android
2. **State management** với Flow hoạt động tốt
3. **Performance** tốt hơn expected
4. **Desktop-specific features** (cursor, drag-n-drop) dễ implement
5. **Material3** components đầy đủ và đẹp

## 🔮 Tiếp theo

Các tính năng có thể thử thêm:
- [ ] Export to Excel/CSV
- [ ] Import from file
- [ ] Dark/Light theme toggle
- [ ] Multi-window support
- [ ] System tray integration
- [ ] Auto-update mechanism
- [ ] Database integration (SQLite)
- [ ] Print functionality

## 📖 Tài liệu tham khảo

- [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/)
- [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html)
- [Material3 Components](https://developer.android.com/jetpack/compose/designsystems/material3)

---

**Note**: Đây là learning project, code chưa production-ready. Use at your own risk! 😄
