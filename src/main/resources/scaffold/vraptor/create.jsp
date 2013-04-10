<%@ include file="/header.jsp" %>

   <h1>@{entity.getName()}</h1>
   <h2>Create a new @{entity.getName()}</h2>

   <form id="create" name="create" method="post" action="<c:url value='/@{ccEntity}/save'/>">
      <table>
         <tbody>
            @{searchFormWidget}
         </tbody>
      </table>
      <span class="buttons">
         <a href="#" onclick="document.create.submit()" class="btn btn-primary">Save</a>
         <a href="<c:url value="/@{ccEntity}/search"/>" class="btn btn-primary">Cancel</a>
      </span>
   </form>

<%@ include file="/footer.jsp" %>
