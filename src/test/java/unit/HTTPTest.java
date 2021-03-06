package unit;

import com.coveros.selenified.services.HTTP;
import com.coveros.selenified.services.Request;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

import static org.testng.Assert.*;

public class HTTPTest {

    @Test
    public void useCredentialsEmptyTest() {
        HTTP http = new HTTP(null, "Service");
        assertFalse(http.useCredentials());
    }

    @Test
    public void useCredentialsBothTest() {
        HTTP http = new HTTP(null, "Service", "User", "Pass");
        assertTrue(http.useCredentials());
    }

    @Test
    public void useCredentialsNeitherTest() {
        HTTP http = new HTTP(null, "Service", "", "");
        assertFalse(http.useCredentials());
    }

    @Test
    public void useCredentialsUserTest() {
        HTTP http = new HTTP(null, "Service", "User", "");
        assertFalse(http.useCredentials());
    }

    @Test
    public void useCredentialsPassTest() {
        HTTP http = new HTTP(null, "Service", "", "Pass");
        assertFalse(http.useCredentials());
    }

    @Test
    public void buildStringNullParamTest() {
        HTTP http = new HTTP(null, "Service");
        assertEquals(http.getRequestParams(null), "");
    }

    @Test
    public void buildStringEmptyParamTest() {
        HTTP http = new HTTP(null, "Service");
        assertEquals(http.getRequestParams(new Request()), "");
    }

    @Test
    public void buildStringNoParamTest() {
        HTTP http = new HTTP(null, "Service");
        assertEquals(http.getRequestParams(new Request().setUrlParams(new HashMap<>())), "");
    }

    @Test
    public void buildStringSingleParamTest() {
        HTTP http = new HTTP(null, "Service");
        Map<String, Object> params = new HashMap<>();
        params.put("hello", "world");
        assertEquals(http.getRequestParams(new Request().setUrlParams(params)), "?hello=world");
    }

    @Test
    public void buildStringMultipleParamsTest() {
        HTTP http = new HTTP(null, "Service");
        Map<String, Object> params = new HashMap<>();
        params.put("hello", "world");
        params.put("john", 5);
        assertEquals(http.getRequestParams(new Request().setUrlParams(params)), "?john=5&hello=world");
    }

    @Test
    public void checkAddingCredentialsTest() {
        HTTP http = new HTTP(null, "Service");
        http.addCredentials("User", "Pass");
        assertTrue(http.useCredentials());
    }

    @Test
    public void checkAddingUserCredentialsTest() {
        HTTP http = new HTTP(null, "Service");
        http.addCredentials("User", "");
        assertFalse(http.useCredentials());
    }

    @Test
    public void checkAddingPassCredentialsTest() {
        HTTP http = new HTTP(null, "Service");
        http.addCredentials("", "Pass");
        assertFalse(http.useCredentials());
    }

    @Test
    public void checkAddingNoCredentialsTest() {
        HTTP http = new HTTP(null, "Service");
        http.addCredentials("", "");
        assertFalse(http.useCredentials());
    }

    @Test
    public void checkAddingBaseUrl() {
        HTTP http = new HTTP(null, "Service");
        assertEquals(http.getServiceBaseUrl(), "Service");
    }

    @Test
    public void checkAddingNullBaseUrl() {
        HTTP http = new HTTP(null, null);
        assertNull(http.getServiceBaseUrl());
    }

    @Test
    public void checkAddingEmptyBaseUrl() {
        HTTP http = new HTTP(null, "");
        assertEquals(http.getServiceBaseUrl(), "");
    }

    @Test
    public void getHeadersDefaultTest() {
        HTTP http = new HTTP(null, "");
        Map<String, String> map = new HashMap<>();
        map.put("Accept", "application/json");
        map.put("Content-length", "0");
        map.put("Content-Type", "application/json; charset=UTF-8");
        assertEquals(http.getHeaders(), map);
    }

    @Test
    public void getHeadersExtraTest() {
        HTTP http = new HTTP(null, "");
        Map<String, Object> map = new HashMap<>();
        map.put("Accept", "application/json");
        map.put("Content-length", "0");
        map.put("Content-Type", "application/json; charset=UTF-8");
        map.put("Age", 1234);
        Map<String, Object> headers = new HashMap<>();
        headers.put("Age", 1234);
        http.addHeaders(headers);
        assertEquals(http.getHeaders(), map);
    }

    @Test
    public void getHeadersOverrideTest() {
        HTTP http = new HTTP(null, "");
        Map<String, String> map = new HashMap<>();
        map.put("Accept", "application/xml");
        map.put("Content-length", "0");
        map.put("Content-Type", "application/json; charset=UTF-8");
        Map<String, Object> headers = new HashMap<>();
        headers.put("Accept", "application/xml");
        http.addHeaders(headers);
        assertEquals(http.getHeaders(), map);
    }

    @Test
    public void addHeadersDefaultTest() {
        HTTP http = new HTTP(null, "");
        Map<String, String> map = new HashMap<>();
        map.put("Accept", "application/json");
        map.put("Content-length", "0");
        map.put("Content-Type", "application/json; charset=UTF-8");
        http.addHeaders(new HashMap<>());
        assertEquals(http.getHeaders(), map);
    }

    @Test
    public void addHeadersResetTest() {
        HTTP http = new HTTP(null, "");
        Map<String, String> map = new HashMap<>();
        map.put("Accept", "application/json");
        map.put("Content-length", "0");
        map.put("Content-Type", "application/json; charset=UTF-8");
        Map<String, Object> headers = new HashMap<>();
        headers.put("Accept", "application/xml");
        http.addHeaders(headers);
        http.resetHeaders();
        assertEquals(http.getHeaders(), map);
    }
}