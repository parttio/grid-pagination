package org.vaadin.klaudeta;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

import java.util.ArrayList;
import java.util.List;

@Route("")
public class DemoView extends Div {

    public DemoView() {
        add(new Span("Lit Pagination"));
        add(new LitPagination());
        add(new Span("Paginated Grid"));

        PaginatedGrid<Address, ?> grid = new PaginatedGrid<>();

        add(grid);

        grid.addColumn(Address::getId).setHeader("ID");
        grid.addColumn(Address::getCountry).setHeader("Country").setSortable(true);
        grid.addColumn(Address::getState).setHeader("State").setSortable(true);
        grid.addColumn(Address::getName).setHeader("Name").setSortable(true);
        grid.addColumn(Address::getAddress).setHeader("Address").setSortable(true);

        List<Address> adresses = new ArrayList<>();
        for (int i = 0; i < 100; i++) {
            Address address = new Address();
            address.setAddress("Ruukinkatu");
            address.setCountry("Finland");
            address.setState("VS");
            address.setName("Name " + i);
            address.setId("" + i);
            adresses.add(address);
        }

        grid.setItems(adresses);

        // Sets the max number of items to be rendered on the grid for each page
        grid.setPageSize(7);

        // Sets how many pages should be visible on the pagination before and/or after the current selected page
        grid.setPaginatorSize(5);

    }
}
