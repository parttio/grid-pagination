# Grid Pagination

This addon adds pagination feature to the Vaadin 10 Grid component. To do so, it extends the existing Vaadin 10 Grid and creates a custom PaginatedGrid

An internal PlutoniumPagination component is created by integrating the plutonium-pagination web component from https://github.com/Klaudeta/plutonium-pagination

## Usage instructions

To use the pagination feature on the grid you just have to use PaginatedGrid component instead of the standard Vaadin 10 Grid as follows: 

```
	PaginatedGrid<Address> grid = new PaginatedGrid<>();

	grid.addColumn(Address::getId).setHeader("ID");
	grid.addColumn(Address::getCountry).setHeader("Country").setSortable(true);
	grid.addColumn(Address::getState).setHeader("State").setSortable(true);
	grid.addColumn(Address::getName).setHeader("Name").setSortable(true);
	grid.addColumn(Address::getAddress).setHeader("Address").setSortable(true);

	grid.setItems(dataProvider.getItems());
	
	// Sets the max number of items to be rendered on the grid for each page
	grid.setPageSize(16);
	
	// Sets how many pages should be visible on the pagination before and/or after the current selected page
	grid.setPaginatorSize(5);
		
```
If you want a specific page to be selected on the pagination:

```
	grid.setPage(4);
	
```
