package com.example.solrj;

import org.apache.solr.client.solrj.SolrClient;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrClient;
import org.apache.solr.client.solrj.request.AbstractUpdateRequest;
import org.apache.solr.client.solrj.request.ContentStreamUpdateRequest;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.client.solrj.response.UpdateResponse;
import org.apache.solr.common.SolrDocument;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.SolrInputDocument;
import org.apache.solr.common.params.MapSolrParams;
import org.apache.solr.common.util.NamedList;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static org.skyscreamer.jsonassert.JSONAssert.assertEquals;

@SpringBootTest
class SolrjApplicationTests {

	private static final String NUM_INDEXED_DOCUMENTS = "";

	/**
	 * solr 기본 데이터 추출
	 * */
	@Test
	public void solrConnectTest() throws SolrServerException, IOException {
		final SolrClient client = getSolrClient();

		final Map<String, String> queryParamMap = new HashMap<String, String>();
		queryParamMap.put("q", "*:*");
		//queryParamMap.put("fl", "id, name");
		MapSolrParams queryParams = new MapSolrParams(queryParamMap);

		final QueryResponse response = client.query("dmc", queryParams);
		final SolrDocumentList documents = response.getResults();

		assertEquals(NUM_INDEXED_DOCUMENTS, documents.getNumFound());
		for(SolrDocument document : documents) {
			//assertTrue(document.getFieldNames().contains("IDX"));
			//System.out.println("IDX : "+document.getFieldValue("IDX"));
			System.out.println("content : "+document.getFieldValue("content"));
			//assertTrue(document.getFieldNames().contains("name"));
		}

	}

	/**
	 * solr 기본 색인 추가
	 * 배치돌며 반복문으로 넣거나 크롤링 하며 실시간으로 넣어도 될듯
	 *
	 * */
	@Test
	public void addIndex() throws SolrServerException, IOException {
		final SolrClient client = getSolrClient();
		final SolrInputDocument doc = new SolrInputDocument();
		doc.addField("IDX", "9998");
		doc.addField("DR_CODE", "DR_CODE_TEST");
		doc.addField("TYPE_CODE", "TYPE_CODE_TEST");
		doc.addField("REPORT_NAME", "TEST 테스트 REPORT");

		final UpdateResponse updateResponse = client.add("dmc", doc);
		client.commit("dmc");
	}


	/**
	 * 로컬 PDF파일 인덱싱
	 * literal.id값 중요, 중복해서 색인하면 덮어쓰게 됨
	 *
	 * */
	@Test
	public void pdfIndex() throws SolrServerException, IOException {
		//String urlString = "http://13.125.245.99:8983/solr/dmc";
		//HttpSolrClient solr = new HttpSolrClient.Builder(urlString).build();

		File file = new File("C:\\Users\\user\\Downloads\\리포트테스트.pdf");

		ContentStreamUpdateRequest req = new ContentStreamUpdateRequest("/update/extract");

		req.addFile(file,"application/pdf");
		req.setParam("literal.id", "doc1");
		req.setAction(AbstractUpdateRequest.ACTION.COMMIT, true, true);
		//solr.request(req);
		getSolrClient().request(req,"dmc");
	}


	public static SolrClient getSolrClient() {
		final String solrUrl = "http://13.125.245.99:8983/solr";
		return new HttpSolrClient.Builder(solrUrl)
				.withConnectionTimeout(10000)
				.withSocketTimeout(60000)
				.build();
	}

	private void assertTrue(boolean id) {
	}

	private void assertEquals(String numIndexedDocuments, long numFound) {
	}
}
