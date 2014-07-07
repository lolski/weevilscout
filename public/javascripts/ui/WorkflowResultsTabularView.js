/**
 * Created with IntelliJ IDEA.
 * User: lolski
 * Date: 10/27/12
 * Time: 2:50 AM
 * To change this template use File | Settings | File Templates.
 */

function WorkflowResultsTabularView(title) {
    this.title = title
    //this.container.textContent = this.title;
    this.table = null;
    this.dataSource = null;
    this.columns = [{
            key: "id",
            sortable: true,
            resizeable: true
        },
        {
            key: "name",
            sortable: true,
            resizeable: true
        },
        {
            key: "status",
            sortable: true,
            resizeable: true
        },
        {
            key: "start",
            formatter: YAHOO.widget.DataTable.formatDate,
            sortable: true,
            sortOptions: {
                defaultDir: YAHOO.widget.DataTable.CLASS_DESC
            },
            resizeable: true
        },
        {
            key: "duration",
            sortable: true,
            resizeable: true
        },
        {
            key: "results",
            sortable: false,
            resizeable: true
        }
        //{key:"executed by"	, sortable:true, resizeable:true}
    ];
    this.queryFrequency = 2000;
}

WorkflowResultsTabularView.prototype.setDataSource = function(dataSource) {
    this.dataSource = new YAHOO.util.DataSource(dataSource);
    this.dataSource.responseType = YAHOO.util.DataSource.TYPE_JSARRAY;
    this.dataSource.responseSchema = {
        fields: ["id", "name", "status", "start", "duration", "results"]
    };
}

WorkflowResultsTabularView.prototype.displayTable = function(parent) {
    this.table = new YAHOO.widget.DataTable(parent, this.columns, this.dataSource, {
        caption: this.title
    });
}
WorkflowResultsTabularView.prototype.query = function() {
    // for each
}