/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package servlets.org.indes.webservice.courses;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author xtecuan
 */
public class CoursesServlet extends HttpServlet {

    public static final String KNLSYSTEM_HOST = "hqdaknl01";
    public static final int KNLSYSTEM_PORT = 9083;
    public static final String KNLSYSTEM_COURSES_WS_URL = "http://" + KNLSYSTEM_HOST + ":" + KNLSYSTEM_PORT + "/webservice/Courses.cfc?wsdl";
    public static final String KNLSYSTEM_CFC_URL = "http://" + KNLSYSTEM_HOST + ":" + KNLSYSTEM_PORT + "/webservice/Courses.cfc";
    public static final String UTF_8_CHARSET = "UTF-8";
    public static final String XML_CONTENT_TYPE = "text/xml;charset=" + UTF_8_CHARSET;
    public static final String TEXT_CONTENT_TYPE = "text/html;charset=" + UTF_8_CHARSET;

    /**
     * Processes requests for both HTTP <code>GET</code> and <code>POST</code>
     * methods.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    protected void processRequest(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/xml;charset=UTF-8");
        PrintWriter out = response.getWriter();
        try {

            Map<String, Object> params = new HashMap<String, Object>();

            String passCode = request.getParameter("passCode");
            boolean passCodeValidation = false;
            if (passCode != null && passCode.length() > 0) {
                passCodeValidation = true;
            }

            String type = request.getParameter("type");
            boolean typeValidation = false;
            if (type != null && type.length() > 0) {
                typeValidation = true;
            }

            String dateLessThanEventStartDate = request.getParameter("dateLessThanEventStartDate");
            boolean dateLessThanEventStartDateValidation = false;
            if (dateLessThanEventStartDate != null && dateLessThanEventStartDate.length() > 0) {
                dateLessThanEventStartDateValidation = true;
            }

            String dateGreaterThanEventStartDate = request.getParameter("dateGreaterThanEventStartDate");
            boolean dateGreaterThanEventStartDateValidation = false;

            if (dateGreaterThanEventStartDate != null && dateGreaterThanEventStartDate.length() > 0) {
                dateGreaterThanEventStartDateValidation = true;
            }

            String dateLessThanEventEndDate = request.getParameter("dateLessThanEventEndDate");
            boolean dateLessThanEventEndDateValidation = false;
            if (dateLessThanEventEndDate != null && dateLessThanEventEndDate.length() > 0) {
                dateLessThanEventEndDateValidation = true;
            }

            String dateGreaterThanEventEndDate = request.getParameter("dateGreaterThanEventEndDate");
            boolean dateGreaterThanEventEndDateValidation = false;
            if (dateGreaterThanEventEndDate != null && dateGreaterThanEventEndDate.length() > 0) {
                dateGreaterThanEventEndDateValidation = true;
            }

            String method = request.getParameter("method");
            boolean methodValidation = false;
            if (method != null && method.length() > 0) {
                methodValidation = true;
            }

            Enumeration<String> paramNames = request.getParameterNames();

            boolean haveWSDL = false;

            while (paramNames.hasMoreElements()) {
                String currentParamName = paramNames.nextElement();

                if (currentParamName.equalsIgnoreCase("wsdl")) {
                    haveWSDL = true;
                    break;
                }
            }

            int contentLength = request.getContentLength();

            if (haveWSDL && contentLength <= 0 && methodValidation == false) {

                URL url = new URL(KNLSYSTEM_COURSES_WS_URL);
                BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream(), UTF_8_CHARSET));
                String line;

                while ((line = reader.readLine()) != null) {
                    out.println(line);
                }
                reader.close();

            }

            if (haveWSDL && contentLength > 0) {
                String body = getBodyOfXMLMessage(request.getReader());
                String contentType = request.getContentType();
                System.out.println(contentType);
                executeCall(body, contentType, out, getCallURL(KNLSYSTEM_COURSES_WS_URL, params), "POST");
            }

            if (contentLength <= 0 && methodValidation) {
                response.setContentType(TEXT_CONTENT_TYPE);
                out = response.getWriter();
                params.put("method", method);
                if (passCodeValidation) {
                    params.put("passCode", passCode);
                }
                if (typeValidation) {
                    params.put("type", type);
                }
                if (dateLessThanEventStartDateValidation) {
                    params.put("dateLessThanEventStartDate", dateLessThanEventStartDate);
                }

                if (dateGreaterThanEventStartDateValidation) {
                    params.put("dateGreaterThanEventStartDate", dateGreaterThanEventStartDate);
                }

                if (dateLessThanEventEndDateValidation) {
                    params.put("dateLessThanEventEndDate", dateLessThanEventEndDate);
                }

                if (dateGreaterThanEventEndDateValidation) {
                    params.put("dateGreaterThanEventEndDate", dateGreaterThanEventEndDate);
                }
                executeCall(null, TEXT_CONTENT_TYPE, out, getCallURL(KNLSYSTEM_CFC_URL, params), "GET");

            }

        } finally {
            out.close();
        }
    }

    private String getBodyOfXMLMessage(BufferedReader reader) {
        StringBuilder sb = new StringBuilder();

        String bodyl;
        try {
            while ((bodyl = reader.readLine()) != null) {
                sb.append(bodyl);
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }

        return sb.toString();
    }

    private String getCallURL(String baseURL, Map<String, Object> callParams) {
        StringBuilder url = new StringBuilder();
        url.append(baseURL);
        if (!callParams.isEmpty()) {
            url.append("?");
            for (String paramName : callParams.keySet()) {
                url.append(paramName).append("=").append(callParams.get(paramName)).append("&");
            }
        }
        return url.toString();
    }

    private void executeCall(String body, String contentType, PrintWriter out, String knlsystemUrl, String httpMethod) {
        try {
            System.out.println(knlsystemUrl);
            URL url = new URL(knlsystemUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod(httpMethod);

            if (body != null && body.length() > 0) {
                connection.setDoOutput(true);
                connection.setRequestProperty("SOAPAction", "getOutputs");
                connection.setRequestProperty("Content-Type", contentType);
                OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
                writer.write(body);
                writer.close();
            }
            System.out.println(connection.getResponseMessage());
            if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedReader reader1 = new BufferedReader(new InputStreamReader(connection.getInputStream(), UTF_8_CHARSET));
                String line1;

                while ((line1 = reader1.readLine()) != null) {
                    out.println(line1);
                }
                reader1.close();
            } else {
                out.println("Error al establecer la conexion");
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
        }
    }

// <editor-fold defaultstate="collapsed" desc="HttpServlet methods. Click on the + sign on the left to edit the code.">
    /**
     * Handles the HTTP <code>GET</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Handles the HTTP <code>POST</code> method.
     *
     * @param request servlet request
     * @param response servlet response
     * @throws ServletException if a servlet-specific error occurs
     * @throws IOException if an I/O error occurs
     */
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        processRequest(request, response);
    }

    /**
     * Returns a short description of the servlet.
     *
     * @return a String containing servlet description
     */
    @Override
    public String getServletInfo() {
        return "CoursesServlet proxy for KNLSystem instance: " + KNLSYSTEM_COURSES_WS_URL;
    }// </editor-fold>

}
