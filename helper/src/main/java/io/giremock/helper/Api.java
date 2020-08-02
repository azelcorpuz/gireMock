package io.giremock.helper;

import java.util.List;

public interface Api {

    String get(String request);

    List<String> getAll(String request);

}
