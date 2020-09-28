package org.vaadin.klaudeta;

import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.data.provider.DataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.function.SerializableComparator;
import com.vaadin.flow.shared.Registration;

import java.util.List;
import java.util.Objects;

/**
 * Grid component where scrolling feature is replaced with a pagination
 * component.
 *
 * @param <T>
 * @author klau
 */
public class PaginatedGrid<T> extends Grid<T> {


    private final LitPagination pagination = new LitPagination();
    private Component paginationContainer = null;

    private PaginationLocation paginationLocation = PaginationLocation.BOTTOM;

    private DataProvider<T, ?> dataProvider;


    public PaginatedGrid() {
        super();
        init();
    }

    public PaginatedGrid(Class<T> beanType) {
        super(beanType);
        init();
    }

    private void init() {
        this.dataProvider = super.getDataProvider();
        this.setHeightByRows(true);
        pagination.addPageChangeListener(e -> doCalcs(e.getNewPage()));
        addSortListener(e -> doCalcs(pagination.getPage()));
    }

    /**
     * Sets a container component for the pagination component to be placed within.
     * If a container is set the PaginationLocation will be ignored.
     * @param paginationContainer
     */
    public void setPaginationContainer(Component paginationContainer) {
        this.paginationContainer = paginationContainer;
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        Span wrapper = new Span(pagination);
        wrapper.getElement().getStyle().set("width", "100%");
        wrapper.getElement().getStyle().set("display", "flex");
        wrapper.getElement().getStyle().set("justify-content", "center");

        if (paginationContainer!=null){
            paginationContainer.getElement().insertChild(0, wrapper.getElement());
        } else {
            getParent().ifPresent(p -> {

                int indexOfChild = p.getElement().indexOfChild(this.getElement());

                //this moves the pagination element below the grid
                if (paginationLocation == PaginationLocation.BOTTOM)
                    indexOfChild++;

                p.getElement().insertChild(indexOfChild, wrapper.getElement());
            });
        }
        doCalcs(0);
    }

    private void doCalcs(int newPage) {
        int offset = newPage > 0 ? (newPage - 1) * this.getPageSize() : 0;

        InnerQuery query = new InnerQuery<>(offset);

        pagination.setTotal(dataProvider.size(query));

        super.setDataProvider(DataProvider.fromStream(dataProvider.fetch(query)));

    }

    public void refreshPaginator() {
        if (pagination != null) {
            pagination.setPageSize(getPageSize());
            pagination.setPage(1);
            if (dataProvider != null) {
                doCalcs(pagination.getPage());
            }
            pagination.refresh();
        }
    }

    @Override
    public void setPageSize(int pageSize) {
        super.setPageSize(pageSize);
        refreshPaginator();

    }

    public int getPage() {
        return pagination.getPage();
    }

    public void setPage(int page) {
        pagination.setPage(page);
    }

    /**
     * @return the location of the pagination element
     */
    public PaginationLocation getPaginationLocation() {
        return paginationLocation;
    }

    /**
     * setter of pagination location
     *
     * @param paginationLocation either PaginationLocation.TOP or PaginationLocation.BOTTOM
     */
    public void setPaginationLocation(PaginationLocation paginationLocation) {
        this.paginationLocation = paginationLocation;
    }

    @Override
    public void setHeightByRows(boolean heightByRows) {
        super.setHeightByRows(true);
    }

    /**
     * Sets the count of the pages displayed before or after the current page.
     *
     * @param size
     */
    public void setPaginatorSize(int size) {
        pagination.setPage(1);
        pagination.setPaginatorSize(size);
        pagination.refresh();
    }

    /**
     * Sets the texts they are displayed on the paginator. This method is useful
     * when localization of the component is applicable.
     *
     * @param pageText the text to display for the `Page` term in the Paginator
     * @param ofText   the text to display for the `of` term in the Paginator
     */
    public void setPaginatorTexts(String pageText, String ofText) {
        pagination.setPageText(pageText);
        pagination.setOfText(ofText);
    }

    @Override
    public void setDataProvider(DataProvider<T, ?> dataProvider) {
        Objects.requireNonNull(dataProvider, "DataProvider shoul not be null!");

        if (!Objects.equals(this.dataProvider, dataProvider)) {
            this.dataProvider = dataProvider;
            this.dataProvider.addDataProviderListener(event -> {
                refreshPaginator();
            });
            refreshPaginator();
        }

    }

    /**
     * Adds a ComponentEventListener to be notified with a PageChangeEvent each time
     * the selected page changes.
     *
     * @param listener to be added
     * @return registration to unregister the listener from the component
     */
    public Registration addPageChangeListener(ComponentEventListener<LitPagination.PageChangeEvent> listener) {
        return pagination.addPageChangeListener(listener);
    }

    /**
     * Enumeration to define the location of the element relative to the grid
     **/
    public enum PaginationLocation {TOP, BOTTOM}

    private class InnerQuery<F> extends Query<T, F> {

        InnerQuery() {
            this(0);
        }

        InnerQuery(int offset) {
            super(offset, getPageSize(), getDataCommunicator().getBackEndSorting(), getDataCommunicator().getInMemorySorting(), null);
        }

        InnerQuery(int offset, List<QuerySortOrder> sortOrders, SerializableComparator<T> serializableComparator) {
            super(offset, getPageSize(), sortOrders, serializableComparator, null);
        }

    }

}
