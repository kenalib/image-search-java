package example;

import javax.servlet.http.Part;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;


class DummyFile implements Part {
    private String fileName;

    DummyFile(String fileName) {
        this.fileName = fileName;
    }

    @Override
    public InputStream getInputStream() throws IOException {
        if (!fileName.equals("")) {
            return Resources.getInputStream(fileName);
        } else {
            return new InputStream() {
                @Override
                public int read() throws IOException {
                    return -1;
                }
            };
        }
    }

    @Override
    public String getContentType() {
        return null;
    }

    @Override
    public String getName() {
        return null;
    }

    @Override
    public String getSubmittedFileName() {
        return null;
    }

    @Override
    public long getSize() {
        return 0;
    }

    @Override
    public void write(String s) throws IOException {

    }

    @Override
    public void delete() throws IOException {

    }

    @Override
    public String getHeader(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaders(String s) {
        return null;
    }

    @Override
    public Collection<String> getHeaderNames() {
        return null;
    }
}
