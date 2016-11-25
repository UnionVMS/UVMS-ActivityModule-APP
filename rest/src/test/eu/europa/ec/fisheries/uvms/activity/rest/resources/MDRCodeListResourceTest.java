package eu.europa.ec.fisheries.uvms.activity.rest.resources;

import eu.europa.ec.fisheries.mdr.repository.MdrRepository;
import eu.europa.ec.fisheries.uvms.rest.dto.PaginatedResponse;
import eu.europa.ec.fisheries.uvms.rest.dto.ResponseDto;
import eu.europa.ec.fisheries.uvms.rest.dto.SearchRequestDto;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.internal.util.reflection.Whitebox;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.core.Response;
import java.util.HashMap;
import java.util.Map;
import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

/**
 * Created by georgige on 11/24/2016.
 */
public class MDRCodeListResourceTest {

    private MDRCodeListResource codeListResource;

    @Mock
    private MdrRepository mdrRepositoryMock;

    @Mock
    private HttpServletRequest requestMock;

    @org.junit.Before
    public void setUp() throws Exception {
        codeListResource = new MDRCodeListResource();
        MockitoAnnotations.initMocks(this);
        Whitebox.setInternalState(codeListResource, "mdrService", mdrRepositoryMock);
    }

    @org.junit.After
    public void tearDown() throws Exception {
        codeListResource = null;
    }

    @org.junit.Test
    public void findCodeListByAcronymFilterredByFilterMissingRequiredParam1() throws Exception {
        SearchRequestDto requestDto = mockSearchRequestDto();
        requestDto.getCriteria().remove("acronym");
        Response response = codeListResource.findCodeListByAcronymFilterredByFilter(requestMock, requestDto);
        Object entity = response.getEntity();
        assertTrue(entity instanceof ResponseDto);
        assertEquals("missing_required_parameter_acronym", ((ResponseDto)entity).getMsg());
        assertEquals(500, ((ResponseDto)entity).getCode());
    }

    @org.junit.Test
    public void findCodeListByAcronymFilterredByFilterMissingRequiredParam3() throws Exception {
        SearchRequestDto requestDto = mockSearchRequestDto();
        requestDto.setCriteria(null);
        Response response = codeListResource.findCodeListByAcronymFilterredByFilter(requestMock, requestDto);
        Object entity = response.getEntity();
        assertTrue(entity instanceof ResponseDto);
        assertEquals("missing_required_criteria", ((ResponseDto)entity).getMsg());
        assertEquals(500, ((ResponseDto)entity).getCode());
    }


    @org.junit.Test
    public void findCodeListByAcronymFilterredByFilterSUCCESS() throws Exception {
        SearchRequestDto requestDto = mockSearchRequestDto();
        Response response = codeListResource.findCodeListByAcronymFilterredByFilter(requestMock, requestDto);
        verify(mdrRepositoryMock, times(1)).findCodeListItemsByAcronymAndFilter("TEST", 0, 100, "column_name", true, "someText", "someAttr");
        verify(mdrRepositoryMock, times(1)).countCodeListItemsByAcronymAndFilter("TEST", "someText", "someAttr");
        Object entity = response.getEntity();
        assertTrue(entity instanceof PaginatedResponse);
        assertEquals(200, ((PaginatedResponse)entity).getCode());
        assertEquals(0, ((PaginatedResponse)entity).getTotalItemsCount());
        assertEquals(0, ((PaginatedResponse)entity).getResultList().size());
    }

    private SearchRequestDto mockSearchRequestDto() {
        SearchRequestDto requestDto = new SearchRequestDto();
        SearchRequestDto.PaginationDto paginationDto = requestDto.new PaginationDto();
        paginationDto.setOffset(0);
        paginationDto.setPageSize(100);

        SearchRequestDto.SortingDto sortingDto = requestDto.new SortingDto();
        sortingDto.setReversed(true);
        sortingDto.setSortBy("column_name");

        requestDto.setPagination(paginationDto);
        requestDto.setSorting(sortingDto);

        Map<String, String> criteria = new HashMap<>();
        criteria.put("acronym", "TEST");
        criteria.put("filter", "someText");
        criteria.put("searchAttribute", "someAttr");

        requestDto.setCriteria(criteria);

        return requestDto;
    }
}