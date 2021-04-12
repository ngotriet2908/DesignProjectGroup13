package com.group13.tcsprojectgrading.models.rubric;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.data.annotation.Id;
import org.springframework.format.annotation.DateTimeFormat;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Stack;

public class Rubric {
    @Id
    private Long id;
    private List<Element> children;
    private int criterionCount;

    @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
    private Date lastModified;

    public Rubric(Long id, List<Element> children, int criterionCount) {
        this.id = id;
        this.children = children;
        this.criterionCount = criterionCount;
        this.lastModified = new Date();
    }

    public Rubric(Long id) {
        this.id = id;
        this.children = new ArrayList<>();
        this.criterionCount = 0;
        this.lastModified = new Date();
    }

    public Rubric() {
    }

    public Date getLastModified() {
        return lastModified;
    }

    public void setLastModified(Date lastModified) {
        this.lastModified = lastModified;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public List<Element> getChildren() {
        return children;
    }

    public List<Element> fetchAllCriteria() {
        Stack<Element> stack = new Stack<>();
        stack.addAll(this.children);
        List<Element> criteria = new ArrayList<>();

        while(stack.size() > 0) {
            Element element = stack.pop();
            if (element.content.type.equals(RubricContent.CRITERION_TYPE)) {
                criteria.add(element);
            } else if (element.content.type.equals(RubricContent.BLOCK_TYPE)) {
                stack.addAll(element.children);
            }
        }
        return criteria;
    }

    public void setChildren(List<Element> children) {
        this.children = children;
    }

    public void setCriterionCount(int criterionCount) {
        this.criterionCount = criterionCount;
    }

    public int getCriterionCount() {
        return criterionCount;
    }


    public static void main(String[] args) throws JsonProcessingException {
        // TODO: remove object mapper
        ObjectMapper objectMapper = new ObjectMapper();
        String json = "{\n" +
                "    \"id\": \"168\",\n" +
                "  \"children\": [\n" +
                "    {\n" +
                "      \"content\": {\n" +
                "        \"id\": \"f1889f48-5d97-4771-9c83-3b18d5b97283\",\n" +
                "        \"type\": \"0\",\n" +
                "        \"title\": \"Crucial Requirement\"\n" +
                "      },\n" +
                "      \"children\": [\n" +
                "        {\n" +
                "          \"content\": {\n" +
                "            \"id\": \"5da62319-bbbc-4691-b9bd-c76835633ab6\",\n" +
                "            \"type\": \"1\",\n" +
                "            \"title\": \"Works using reference server/own client as well as own server/reference client\",\n" +
                "            \"text\": \"<p>A standard game can be played on both client and server in conjunction with the reference server and client, respectively.</p>\",\n" +
                "            \"grade\": {\n" +
                "              \"min\": 0,\n" +
                "              \"max\": 1,\n" +
                "              \"step\": 1\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"content\": {\n" +
                "            \"id\": \"9d9943c6-6884-40b4-91c6-6de00e0ef890\",\n" +
                "            \"type\": \"1\",\n" +
                "            \"title\": \"Has human player\",\n" +
                "            \"text\": \"<p>The client can play as a human player, controlled by the user.</p>\",\n" +
                "            \"grade\": {\n" +
                "              \"min\": 0,\n" +
                "              \"max\": 1,\n" +
                "              \"step\": 1\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"content\": {\n" +
                "        \"id\": \"b15d10c5-0549-4645-b2b5-669aeab66da5\",\n" +
                "        \"type\": \"0\",\n" +
                "        \"title\": \"Important Requirement\"\n" +
                "      },\n" +
                "      \"children\": [\n" +
                "        {\n" +
                "          \"content\": {\n" +
                "            \"id\": \"d8a75022-73ee-4c5b-9b40-2ef59acef73d\",\n" +
                "            \"type\": \"1\",\n" +
                "            \"title\": \"Ask user for port on server start. If port is unavailable ask again\",\n" +
                "            \"text\": \"<p>When the server is started, it will ask the user to input a port number where it will listen to. If this number is already in use, the server will ask again.</p>\",\n" +
                "            \"grade\": {\n" +
                "              \"min\": 0,\n" +
                "              \"max\": 1,\n" +
                "              \"step\": 1\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"content\": {\n" +
                "            \"id\": \"a7667e09-25f9-4321-a370-168e679d4b1c\",\n" +
                "            \"type\": \"1\",\n" +
                "            \"title\": \"Ask for IP-address and port on client start\",\n" +
                "            \"text\": \"<p>When the client is started, it should ask the user for the IP-address and port number of the server to connect to.</p>\",\n" +
                "            \"grade\": {\n" +
                "              \"min\": 0,\n" +
                "              \"max\": 1,\n" +
                "              \"step\": 1\n" +
                "            }\n" +
                "          }\n" +
                "        }\n" +
                "      ]\n" +
                "    },\n" +
                "    {\n" +
                "      \"content\": {\n" +
                "        \"id\": \"fb05f115-549a-4330-996c-56d9274c1134\",\n" +
                "        \"type\": \"0\",\n" +
                "        \"title\": \"Software\"\n" +
                "      },\n" +
                "      \"children\": [\n" +
                "        {\n" +
                "          \"content\": {\n" +
                "            \"id\": \"0647fbf6-a1dd-4759-8c98-2ff4ca16c70b\",\n" +
                "            \"type\": \"0\",\n" +
                "            \"title\": \"Product Quality\"\n" +
                "          },\n" +
                "          \"children\": [\n" +
                "            {\n" +
                "              \"content\": {\n" +
                "                \"id\": \"e576cdfe-0cad-4986-a948-8d0dde4c958e\",\n" +
                "                \"type\": \"1\",\n" +
                "                \"title\": \"Client TUI\",\n" +
                "                \"text\": \"<p>1 : The TUI provides insufficient information to the user to play a game. It is not possible to play a game without consulting the source code or the developers of the game</p>\",\n" +
                "                \"grade\": {\n" +
                "                  \"min\": 1,\n" +
                "                  \"max\": 8,\n" +
                "                  \"step\": 1\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            {\n" +
                "              \"content\": {\n" +
                "                \"id\": \"841c68a9-d7de-46ea-bff3-a40564408a88\",\n" +
                "                \"type\": \"1\",\n" +
                "                \"title\": \"Game Stability\",\n" +
                "                \"text\": \"<p>The client and the server never crash, show stacktraces to the user or hang, even when intentionally malformed input is given by the user.</p>\\n<p>The client and server always agree on the current game state, i.e. client and server stay in sync.</p>\",\n" +
                "                \"grade\": {\n" +
                "                  \"min\": 1,\n" +
                "                  \"max\": 8,\n" +
                "                  \"step\": 1\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        },\n" +
                "        {\n" +
                "          \"content\": {\n" +
                "            \"id\": \"cf9d30c6-6bea-4803-ae7e-a23caabee2b1\",\n" +
                "            \"type\": \"1\",\n" +
                "            \"title\": \"Packaging\",\n" +
                "            \"text\": \"<p>Java source code without compilation errors (including used libraries as JARs).</p>\\n<p>Clear instructions on the usage of the server and the client through a README file</p>\\n<p>Javadoc documentation exported in the form of HTML pages in a separate directory structure.</p>\\n<p>Executable JAR-file of the submitted source code for both the client and the server.</p>\",\n" +
                "            \"grade\": {\n" +
                "              \"min\": 0,\n" +
                "              \"max\": 10,\n" +
                "              \"step\": 1\n" +
                "            }\n" +
                "          }\n" +
                "        },\n" +
                "        {\n" +
                "          \"content\": {\n" +
                "            \"id\": \"fb2974d0-01b4-435d-bdc9-b4c95185b263\",\n" +
                "            \"type\": \"0\",\n" +
                "            \"title\": \"Code Quality and Testing\"\n" +
                "          },\n" +
                "          \"children\": [\n" +
                "            {\n" +
                "              \"content\": {\n" +
                "                \"id\": \"6c13e719-bfb3-4731-a877-3c2701c14482\",\n" +
                "                \"type\": \"1\",\n" +
                "                \"title\": \"Code Quality\",\n" +
                "                \"text\": \"<p>One of the points in “excellent” is structurally violated, or three or four points are incidentally violated.</p>\",\n" +
                "                \"grade\": {\n" +
                "                  \"min\": 0,\n" +
                "                  \"max\": 8,\n" +
                "                  \"step\": 1\n" +
                "                }\n" +
                "              }\n" +
                "            },\n" +
                "            {\n" +
                "              \"content\": {\n" +
                "                \"id\": \"0a7e3b23-5d66-4497-ac60-25d2d4b6c404\",\n" +
                "                \"type\": \"1\",\n" +
                "                \"title\": \"Tests\",\n" +
                "                \"text\": \"<p>All non-UI related classes are extensively tested using unit and integration tests. It is clear from the test code or documentation which tests are unit tests and which tests are integration tests. Every test case has a clearly defined scope. Mocking of input/output streams is used to test the server and client (separately).</p>\",\n" +
                "                \"grade\": {\n" +
                "                  \"min\": 0,\n" +
                "                  \"max\": 10,\n" +
                "                  \"step\": 1\n" +
                "                }\n" +
                "              }\n" +
                "            }\n" +
                "          ]\n" +
                "        }\n" +
                "      ]\n" +
                "    }\n" +
                "  ]\n" +
                "}";
        Rubric rubric = objectMapper.readValue(json, Rubric.class);
    }
}
