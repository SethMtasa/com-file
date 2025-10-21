package prac.com_file.security;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationPropertiesScan
@ConfigurationProperties(prefix = "active.directory")
public class ActiveDirectoryProperties {
    private String domain;
    private String url;
    private String rootDn;

    // Getters
    public String getDomain() {
        return domain;
    }

    public String getUrl() {
        return url;
    }

    public String getRootDn() {
        return rootDn;
    }

    // Setters
    public void setDomain(String domain) {
        this.domain = domain;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public void setRootDn(String rootDn) {
        this.rootDn = rootDn;
    }
}
