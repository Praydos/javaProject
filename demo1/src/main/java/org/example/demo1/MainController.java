package org.example.demo1;

//package com.example.inventory;

//package com.example.inventory;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import java.util.List;

public class MainController {
    // -- PRODUCTS --
    @FXML private TableView<Product> productTable;
    @FXML private TableColumn<Product, Integer> colProductId;
    @FXML private TableColumn<Product, String> colProductName;
    @FXML private TableColumn<Product, Integer> colCurrentStock;
    @FXML private TableColumn<Product, Integer> colReorderLevel;

    @FXML private TextField txtName;
    @FXML private TextField txtDescription;
    @FXML private TextField txtCategoryId;
    @FXML private TextField txtSupplierId;
    @FXML private TextField txtCostPrice;
    @FXML private TextField txtSellingPrice;
    @FXML private TextField txtReorderLevel;
    @FXML private TextField txtTransactionQty;

    // -- SUPPLIERS --
    @FXML private TableView<Supplier> supplierTable;
    @FXML private TableColumn<Supplier, Integer> colSupplierId;
    @FXML private TableColumn<Supplier, String> colSupplierName;
    @FXML private TableColumn<Supplier, String> colContactPerson;
    @FXML private TableColumn<Supplier, String> colPhone;

    @FXML private TextField txtSupplierName;
    @FXML private TextField txtContactPerson;
    @FXML private TextField txtPhone;
    @FXML private TextField txtEmail;
    @FXML private TextField txtAddress;

    // -- CATEGORIES --
    @FXML private TableView<Category> categoryTable;
    @FXML private TableColumn<Category, Integer> colCategoryId;
    @FXML private TableColumn<Category, String> colCategoryName;

    @FXML private TextField txtCategoryName;
    @FXML private TextField txtCategoryDescription;

    @FXML
    public void initialize() {
        // ========== PRODUCT TABLE SETUP ==========
        colProductId.setCellValueFactory(new PropertyValueFactory<>("productId"));
        colProductName.setCellValueFactory(new PropertyValueFactory<>("productName"));
        colCurrentStock.setCellValueFactory(new PropertyValueFactory<>("currentStock"));
        colReorderLevel.setCellValueFactory(new PropertyValueFactory<>("reorderLevel"));

        // CUSTOM CELL FACTORY: HIGHLIGHT LOW STOCK IN RED
        colCurrentStock.setCellFactory(column -> new TableCell<Product, Integer>() {
            @Override
            protected void updateItem(Integer item, boolean empty) {
                super.updateItem(item, empty);

                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(String.valueOf(item));
                    Product product = getTableView().getItems().get(getIndex());
                    // If current stock <= reorder level => highlight in red
                    if (product != null && product.getCurrentStock() <= product.getReorderLevel()) {
                        setStyle("-fx-text-fill: red;");
                    } else {
                        setStyle("-fx-text-fill: black;");
                    }
                }
            }
        });

        // ========== SUPPLIER TABLE SETUP ==========
        colSupplierId.setCellValueFactory(new PropertyValueFactory<>("supplierId"));
        colSupplierName.setCellValueFactory(new PropertyValueFactory<>("supplierName"));
        colContactPerson.setCellValueFactory(new PropertyValueFactory<>("contactPerson"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        // ========== CATEGORY TABLE SETUP ==========
        colCategoryId.setCellValueFactory(new PropertyValueFactory<>("categoryId"));
        colCategoryName.setCellValueFactory(new PropertyValueFactory<>("categoryName"));

        // Initially load data into all tables
        refreshAllTables();
    }

    private void refreshAllTables() {
        // PRODUCTS
        productTable.getItems().setAll(ProductDAO.getAllProducts());

        // SUPPLIERS
        supplierTable.getItems().setAll(SupplierDAO.getAllSuppliers());

        // CATEGORIES
        categoryTable.getItems().setAll(CategoryDAO.getAllCategories());

        // Optionally check for low stock and show a warning alert
        List<Product> lowStock = ProductDAO.getLowStockProducts();
        if (!lowStock.isEmpty()) {
            StringBuilder sb = new StringBuilder("Low Stock Warning:\n");
            for (Product p : lowStock) {
                sb.append(String.format("ID %d - %s (Stock: %d, Reorder Level: %d)\n",
                        p.getProductId(), p.getProductName(),
                        p.getCurrentStock(), p.getReorderLevel()));
            }
            showAlert(Alert.AlertType.WARNING, sb.toString());
        }
    }

    // ---------------------------------------------------------------------------------------------
    // PRODUCT CRUD & TRANSACTIONS
    // ---------------------------------------------------------------------------------------------
    @FXML
    private void onAddProduct() {
        try {
            String name = txtName.getText();
            String desc = txtDescription.getText();
            int catId = Integer.parseInt(txtCategoryId.getText());
            int suppId = Integer.parseInt(txtSupplierId.getText());
            double cost = Double.parseDouble(txtCostPrice.getText());
            double sell = Double.parseDouble(txtSellingPrice.getText());
            int reorder = Integer.parseInt(txtReorderLevel.getText());

            Product newProduct = new Product(0, name, desc, catId, suppId, cost, sell, reorder, 0);
            ProductDAO.insertProduct(newProduct);
            refreshAllTables();
            clearProductFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid numeric input for Product.");
        }
    }

    @FXML
    private void onUpdateProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No product selected for update.");
            return;
        }
        try {
            selected.setProductName(txtName.getText());
            selected.setDescription(txtDescription.getText());
            selected.setCategoryId(Integer.parseInt(txtCategoryId.getText()));
            selected.setSupplierId(Integer.parseInt(txtSupplierId.getText()));
            selected.setCostPrice(Double.parseDouble(txtCostPrice.getText()));
            selected.setSellingPrice(Double.parseDouble(txtSellingPrice.getText()));
            selected.setReorderLevel(Integer.parseInt(txtReorderLevel.getText()));

            ProductDAO.updateProduct(selected);
            refreshAllTables();
            clearProductFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid numeric input for Product.");
        }
    }

    @FXML
    private void onDeleteProduct() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No product selected for deletion.");
            return;
        }
        ProductDAO.deleteProduct(selected.getProductId());
        refreshAllTables();
        clearProductFields();
    }

    @FXML
    private void onStockIn() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No product selected for stock-in.");
            return;
        }
        try {
            int qty = Integer.parseInt(txtTransactionQty.getText());
            if (qty <= 0) {
                showAlert(Alert.AlertType.ERROR, "Quantity must be > 0.");
                return;
            }
            Transaction transaction = new Transaction(0, selected.getProductId(), "IN", qty, "Stock In");
            TransactionDAO.insertTransaction(transaction);
            refreshAllTables();
            clearProductFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid quantity for stock-in.");
        }
    }

    @FXML
    private void onStockOut() {
        Product selected = productTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No product selected for stock-out.");
            return;
        }
        try {
            int qty = Integer.parseInt(txtTransactionQty.getText());
            if (qty <= 0) {
                showAlert(Alert.AlertType.ERROR, "Quantity must be > 0.");
                return;
            }
            Transaction transaction = new Transaction(0, selected.getProductId(), "OUT", qty, "Stock Out");
            TransactionDAO.insertTransaction(transaction);
            refreshAllTables();
            clearProductFields();
        } catch (NumberFormatException e) {
            showAlert(Alert.AlertType.ERROR, "Invalid quantity for stock-out.");
        }
    }

    private void clearProductFields() {
        txtName.clear();
        txtDescription.clear();
        txtCategoryId.clear();
        txtSupplierId.clear();
        txtCostPrice.clear();
        txtSellingPrice.clear();
        txtReorderLevel.clear();
        txtTransactionQty.clear();
    }

    // ---------------------------------------------------------------------------------------------
    // SUPPLIER CRUD
    // ---------------------------------------------------------------------------------------------
    @FXML
    private void onAddSupplier() {
        try {
            String supplierName = txtSupplierName.getText();
            String contactPerson = txtContactPerson.getText();
            String phone = txtPhone.getText();
            String email = txtEmail.getText();
            String address = txtAddress.getText();

            Supplier s = new Supplier(0, supplierName, contactPerson, phone, email, address);
            SupplierDAO.insertSupplier(s);
            refreshAllTables();
            clearSupplierFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error adding supplier: " + e.getMessage());
        }
    }

    @FXML
    private void onUpdateSupplier() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No supplier selected for update.");
            return;
        }
        try {
            selected.setSupplierName(txtSupplierName.getText());
            selected.setContactPerson(txtContactPerson.getText());
            selected.setPhone(txtPhone.getText());
            selected.setEmail(txtEmail.getText());
            selected.setAddress(txtAddress.getText());

            SupplierDAO.updateSupplier(selected);
            refreshAllTables();
            clearSupplierFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error updating supplier: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteSupplier() {
        Supplier selected = supplierTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No supplier selected for deletion.");
            return;
        }
        SupplierDAO.deleteSupplier(selected.getSupplierId());
        refreshAllTables();
        clearSupplierFields();
    }

    private void clearSupplierFields() {
        txtSupplierName.clear();
        txtContactPerson.clear();
        txtPhone.clear();
        txtEmail.clear();
        txtAddress.clear();
    }

    // ---------------------------------------------------------------------------------------------
    // CATEGORY CRUD
    // ---------------------------------------------------------------------------------------------
    @FXML
    private void onAddCategory() {
        try {
            String catName = txtCategoryName.getText();
            String desc = txtCategoryDescription.getText();

            Category c = new Category(0, catName, desc);
            CategoryDAO.insertCategory(c);
            refreshAllTables();
            clearCategoryFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error adding category: " + e.getMessage());
        }
    }

    @FXML
    private void onUpdateCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No category selected for update.");
            return;
        }
        try {
            selected.setCategoryName(txtCategoryName.getText());
            selected.setDescription(txtCategoryDescription.getText());

            CategoryDAO.updateCategory(selected);
            refreshAllTables();
            clearCategoryFields();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Error updating category: " + e.getMessage());
        }
    }

    @FXML
    private void onDeleteCategory() {
        Category selected = categoryTable.getSelectionModel().getSelectedItem();
        if (selected == null) {
            showAlert(Alert.AlertType.WARNING, "No category selected for deletion.");
            return;
        }
        CategoryDAO.deleteCategory(selected.getCategoryId());
        refreshAllTables();
        clearCategoryFields();
    }

    private void clearCategoryFields() {
        txtCategoryName.clear();
        txtCategoryDescription.clear();
    }

    // ---------------------------------------------------------------------------------------------
    // HELPER METHOD: Show Alert
    // ---------------------------------------------------------------------------------------------
    private void showAlert(Alert.AlertType type, String message) {
        Alert alert = new Alert(type, message, ButtonType.OK);
        alert.showAndWait();
    }
}
