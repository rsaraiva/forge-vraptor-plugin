<%@ include file="/header.jsp" %>

   <h1>@{entity.getName()}</h1>
   <h2>Edit existing @{entity.getName()}</h2>

   <form id="edit" name="edit" method="post" action="<c:url value='/@{ccEntity}/save'/>">
      <input id="id" type="hidden" name="@{ccEntity}.id" value="@{elIdValue}" />
      <table>
         <tbody>
            @{searchFormWidget}
         </tbody>
      </table>
      <span class="buttons">
         <a href="#" onclick="document.edit.submit()" class="btn btn-primary">Save</a>
         <a href="<c:url value='/@{ccEntity}/search'/>" class="btn btn-primary">Cancel</a>
         <a href="#" onclick="document.edit.action='<c:url value="/@{ccEntity}/delete"/>';document.edit.submit()" class="btn btn-primary">Delete</a>
      </span>
   </form>

<%@ include file="/footer.jsp" %>
