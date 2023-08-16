package com.cherry.rabbitmq.inspector;

import feign.Feign;
import io.kubernetes.client.openapi.ApiClient;
import io.kubernetes.client.openapi.ApiException;
import io.kubernetes.client.openapi.Configuration;
import io.kubernetes.client.openapi.apis.CoreV1Api;
import io.kubernetes.client.openapi.models.V1Pod;
import io.kubernetes.client.openapi.models.V1PodList;
import io.kubernetes.client.util.Config;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Component
public class PullerNotifier {

    private final static String STATEFUL_SETS_POD_LABEL = "statefulset.kubernetes.io/pod-name";
//    public static void main(String[] args) throws IOException {
//    }

    public void notifyPuller() throws IOException {

        // 假設你已經獲得了一系列的DNS名稱
        List<String> podDnsNames = new PullerNotifier().findPodsDns();

        for (String dnsName : podDnsNames) {
            PullClient podClient = Feign.builder()
                    .target(PullClient.class, "http://" + dnsName + ":8080");  // 假設你的服務運行在8080端口

            try {
                String response = podClient.pull();
                System.out.println("Response from " + dnsName + ": " + response);
            } catch (Exception e) {
                System.err.println("Error accessing " + dnsName + ": " + e.getMessage());
            }
        }
    }

    private List<String> findPodsDns() throws IOException {
        String statefulSetName = "rabbit-mq-puller";
        String namespace = "default";
        String serviceName = "rabbit-mq-puller";  // headless service名稱

        // 設置ApiClient
        ApiClient client = Config.defaultClient();
        Configuration.setDefaultApiClient(client);
        CoreV1Api api = new CoreV1Api();

        List<String> podsDns = new ArrayList<>();
        try {
            V1PodList list = api.listNamespacedPod(namespace, null, null, null, null,
                    STATEFUL_SETS_POD_LABEL, null, null, null, null,
                    false);

            for (V1Pod pod : list.getItems()) {
                String podName = pod.getMetadata().getName();
                if (podName.startsWith(statefulSetName)) {
                    String dns = String.format("%s.%s.%s.svc.cluster.local", podName, serviceName, namespace);
                    podsDns.add(dns);

                    System.out.println(dns);
                }
            }

            return podsDns;
        } catch (ApiException e) {
            System.err.println("Exception when calling CoreV1Api#listNamespacedPod: " + e.getResponseBody());
            throw new RuntimeException(e);
        }
    }
}
