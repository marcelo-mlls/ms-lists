package com.unimedsc.ms_list.service;

import com.microsoft.graph.models.ListItem;
import com.microsoft.graph.models.ListItemCollectionResponse;
import com.microsoft.graph.models.Site;
import com.microsoft.graph.serviceclient.GraphServiceClient;
import com.microsoft.kiota.RequestInformation; // Adicione esta importação
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI; // Adicione esta importação
import java.util.List;

@Service
public class GraphListService {

    @Autowired
    private GraphServiceClient graphClient;

    @Value("${graph.oauth.site-hostname}")
    private String siteHostname;

    @Value("${graph.oauth.site-path}")
    private String sitePath;

    @Value("${graph.oauth.list-id}")
    private String listId;

    public List<ListItem> getListItems() {
        try {
            String siteIdentifier = siteHostname + ":/sites/" + sitePath;
            Site site = graphClient.sites().bySiteId(siteIdentifier).get();
            String siteId = site.getId();

            // Construir o objeto de requisição (não a chamada final)
            RequestInformation requestInfo = graphClient
                    .sites()
                    .bySiteId(siteId)
                    .lists()
                    .byListId(listId)
                    .items()
                    .toGetRequestInformation(requestConfiguration -> {
                        requestConfiguration.queryParameters.expand = new String[]{"fields($select=id,Projetos)"};
                    });

            // Imprimir a URL da requisição no console para depuração
            URI requestUri = requestInfo.getUri();
            System.out.println("=================================================");
            System.out.println("URL da API do Graph: " + requestUri.toString());
            System.out.println("=================================================");

            // Agora, faça a chamada real com o mesmo builder
            ListItemCollectionResponse result = graphClient
                    .sites()
                    .bySiteId(siteId)
                    .lists()
                    .byListId(listId)
                    .items()
                    .get(requestConfiguration -> {
                        requestConfiguration.queryParameters.expand = new String[]{"fields($select=id,Projetos)"};
                    });

            if (result != null && result.getValue() != null) {
                List<ListItem> items = result.getValue();
                System.out.println("=================================================");
                System.out.println("Itens encontrados na lista: " + items.size());
                System.out.println("=================================================");

                for (int i = 0; i < items.size(); i++) {
                    ListItem item = items.get(i);
                    System.out.println("\n--- Item " + (i + 1) + " ---");
                    System.out.println("ID do Item: " + item.getId());

                    if (item.getFields() != null && item.getFields().getAdditionalData() != null) {
                        System.out.println("Campos:");
                        item.getFields().getAdditionalData().forEach((key, value) -> {
                            System.out.println("  " + key + ": " + (value != null ? value.toString() : "null"));
                        });
                    } else {
                        System.out.println("  (Sem campos para exibir)");
                    }

                    Object projetosValue = item.getFields().getAdditionalData().get("Projetos");
                    if (projetosValue != null) {
                        System.out.println("Valor da coluna 'Projetos': " + projetosValue.toString());
                    } else {
                        System.out.println("Valor da coluna 'Projetos' é nulo ou não existe.");
                    }
                }
                System.out.println("\n=================================================");
                return items;
            }
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erro ao buscar itens da lista: " + e.getMessage());
        }
        return null;
    }
}