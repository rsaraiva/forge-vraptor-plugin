<%@ include file="/header.jsp" %>

   <h1>Oops</h1>
   <h2>That's going to leave a mark!</h2>

   <h2 class="success">
      Your application is running.
   </h2>

   <p style="text-align: right; padding-top: 150px">
      <a target="_blank" href="http://jboss.org">
         <img src="<c:url value="/resources/jboss-community.png"/>" alt="JBoss and JBoss Community" width="254" height="31" border="0" />
      </a> 
      <br /> To replace this page edit 'src/main/webapp/error.xhtml', or keep Forging!
   </p>

<%@ include file="/footer.jsp" %>