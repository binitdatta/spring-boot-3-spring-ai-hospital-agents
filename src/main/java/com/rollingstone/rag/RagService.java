package com.rollingstone.rag;

import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.util.StreamUtils;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

@Service
public class RagService {

    private final EmbeddingModel embeddings;
    private final VectorStore store;

    public RagService(OpenAiEmbeddingModel embeddings,
                      @Qualifier("localVectorStore") VectorStore store) {
        this.embeddings = embeddings;
        this.store = store;
    }

    public int ingestClasspathMd(String resourcePath, String idPrefix) throws Exception {
        try (InputStream in = getClass().getResourceAsStream(resourcePath)) {
            String content = StreamUtils.copyToString(in, StandardCharsets.UTF_8);
            String[] chunks = content.split("\n## "); // naive chunker
            int count = 0;
            for (int i = 0; i < chunks.length; i++) {
                String chunk = (i == 0) ? chunks[i] : "## " + chunks[i];
                EmbeddingResponse er = embeddings.embedForResponse(List.of(chunk));
                float[] embF = er.getResults().get(0).getOutput(); // 1.0.1 → float[]
                store.upsert(idPrefix + "-" + i, toDouble(embF), chunk, "classpath:" + resourcePath);
                count++;
            }
            return count;
        }
    }

    public List<Map<String, Object>> search(String query, int k) {
        // Keep API usage consistent with ingest()
        EmbeddingResponse er = embeddings.embedForResponse(List.of(query));
        float[] qF = er.getResults().get(0).getOutput(); // float[] in 1.0.1
        double[] q = toDouble(qF);                       // if your VectorStore uses double[]
        return store.topK(q, k);
    }

    private static double[] toDouble(float[] src) {
        double[] dst = new double[src.length];
        for (int i = 0; i < src.length; i++) dst[i] = src[i];
        return dst;
    }
}
