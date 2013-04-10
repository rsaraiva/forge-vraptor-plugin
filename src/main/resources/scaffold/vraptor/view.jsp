<%@ include file="/header.jsp" %>

   <h1>Customer</h1>
   <h2>View existing Customer</h2>

   <table>
      <tbody>
         @{viewWidget}
      </tbody>
   </table>
   <div class="buttons">
      <a href="<c:url value="/@{ccEntity}/search"/>" class="btn btn-primary">View All</a>
      <a href="<c:url value="/@{ccEntity}/edit/@{elIdValue}"/>" class="btn btn-primary">Edit</a>
      <a href="<c:url value="/@{ccEntity}/create"/>" class="btn btn-primary">Create New</a>
   </div>

<%@ include file="/footer.jsp" %>
