package com.wind.bdc.elasticsearchdemo.config;

import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.net.InetAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author hylu.Ivan
 * @date 2021/7/30 上午10:43
 * @description
 */
@Component
@ConfigurationProperties(prefix = "elasticsearch")
public class EsConfig {
    private String   clusterName;
    private String clusterNodes;

    public String getClusterName() {
        return clusterName;
    }

    public void setClusterName(String clusterName) {
        this.clusterName = clusterName;
    }

    public String getClusterNodes() {
        return clusterNodes;
    }

    public void setClusterNodes(String clusterNodes) {
        this.clusterNodes = clusterNodes;
    }

    @Bean
    public Client esClient(EsConfig esConfig) throws Exception{
        TransportClient client = null;
        Settings settings = Settings.settingsBuilder().put("client.transport.sniff", true).put("client.transport.ping_timeout", 5000, TimeUnit.MILLISECONDS)
                .put("cluster.name", esConfig.getClusterName()).build();
        client = TransportClient.builder().settings(settings).build();

        String[] ipArray = esConfig.getClusterNodes().split(",");
        for (String ip : ipArray) {
            String[] ipPort = ip.split(":");
            client.addTransportAddress(new InetSocketTransportAddress(InetAddress.getByName(ipPort[0]), Integer.parseInt(ipPort[1])));
        }

        return client;
    }
}

