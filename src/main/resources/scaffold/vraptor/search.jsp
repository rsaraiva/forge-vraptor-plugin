<%@ include file="/header.jsp" %>

<h1>@{entity.getName()}</h1>
<h2>Search @{entity.getName()} entities</h2>

<form id="search" name="search" method="post" action="<c:url value='/@{ccEntity}/search'/>">
    <span class="search">
        <table>
            <tbody>
                @{searchFormWidget}
            </tbody>
        </table>
        <span class="buttons">
            <a href="#" onclick="document.search.submit()" class="btn btn-primary">Search</a>
            <a href="create" class="btn btn-primary">Create New</a>
        </span>
    </span>
    <table class="data-table">
        <thead>
            <tr>
                @{searchTableHeaderWidget}
            </tr>
        </thead>
        <tbody>
            @{searchTableBodyWidget}
        </tbody>
    </table>
</form>

<%@ include file="/footer.jsp" %>
