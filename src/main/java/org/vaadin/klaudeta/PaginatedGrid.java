package org.vaadin.klaudeta;

import com.vaadin.flow.component.AttachEvent;
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

    private final LitPagination paginaton;

    private PaginationLocation paginationLocation = PaginationLocation.BOTTOM;

    private DataProvider<T, ?> dataProvider;

    public PaginatedGrid() {
	paginaton = new LitPagination();
	this.dataProvider = super.getDataProvider();
	this.setHeightByRows(true);
	paginaton.addPageChangeListener(e -> doCalcs(e.getNewPage()));
	addSortListener(e -> doCalcs(paginaton.getPage()));

    }

    @Override protected void onAttach(AttachEvent attachEvent) {
	super.onAttach(attachEvent);
	getParent().ifPresent(p -> {

	    int indexOfChild = p.getElement().indexOfChild(this.getElement());
	    Span wrapper = new Span(paginaton);
	    wrapper.getElement().getStyle().set("width", "100%");
	    wrapper.getElement().getStyle().set("display", "flex");
	    wrapper.getElement().getStyle().set("justify-content", "center");

	    //this moves the pagination element below the grid
	    if (paginationLocation == PaginationLocation.BOTTOM)
		indexOfChild++;

	    p.getElement().insertChild(indexOfChild, wrapper.getElement());
	});

	doCalcs(0);
    }

    private void doCalcs(int newPage) {
	int offset = newPage > 0 ? (newPage - 1) * this.getPageSize() : 0;

	InnerQuery query = new InnerQuery<>(offset);

	paginaton.setTotal(dataProvider.size(query));

	super.setDataProvider(DataProvider.fromStream(dataProvider.fetch(query)));

    }

    public void refreshPaginator() {
	if (paginaton != null) {
	    paginaton.setPageSize(getPageSize());
	    paginaton.setPage(1);
	    if (dataProvider != null) {
		doCalcs(paginaton.getPage());
	    }
	    paginaton.refresh();
	}
    }

    @Override public void setPageSize(int pageSize) {
	super.setPageSize(pageSize);
	refreshPaginator();

    }

    public int getPage() {
	return paginaton.getPage();
    }

    public void setPage(int page) {
	paginaton.setPage(page);
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

    @Override public void setHeightByRows(boolean heightByRows) {
	super.setHeightByRows(true);
    }

    /**
     * Sets the count of the pages displayed before or after the current page.
     *
     * @param size
     */
    public void setPaginatorSize(int size) {
	paginaton.setPage(1);
	paginaton.setPaginatorSize(size);
	paginaton.refresh();
    }

    /**
     * Sets the texts they are displayed on the paginator. This method is useful
     * when localization of the component is applicable.
     *
     * @param pageText the text to display for the `Page` term in the Paginator
     * @param ofText   the text to display for the `of` term in the Paginator
     */
    public void setPaginatorTexts(String pageText, String ofText) {
	paginaton.setPageText(pageText);
	paginaton.setOfText(ofText);
    }

    @Override public void setDataProvider(DataProvider<T, ?> dataProvider) {
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
	return paginaton.addPageChangeListener(listener);
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
