package constants

/*
*
*	SetJoinData takes sets the specific data points for the joinData slice in order for SQL translation to include data from
*	other data points
*
*	@param joinData: a map where the key is a string representing the table name and any foreign key references, and the value is a slice containing:
  - the name of the table
  - the name of the primary key for that table
  - any data values requested from that table
    @param keys: a slice of strings representing the keys in the joinData map
    @param activeTable: The main table which data is being queried from

*
*
*	Description of the joinData Slice
*	"targetTableName" :	["tablename", "PrimaryKey",	"dataIWant", "moreDataIWant", etc...]
*
* 	targetTableName: Name of table we want "dataIWant" and such from
*	tablename: Name of the table that shares the primary key value with target table
*	PrimaryKey: Name of value that is the SQL Primary key on TargetTableName
*	dataIWant: desired data we want SQL query to include
*/
func SetJoinData(joinData map[string][]string, keys []string, activeTable string) (map[string][]string, []string) {
	switch activeTable {
	case "container":
		joinData["client"] = append(joinData["client"], "container", "client_id", "client_name")
		joinData["location"] = append(joinData["location"], "container", "location_id", "location_name")
		joinData["container_model"] = append(joinData["container_model"], "container", "container_model_name", "liter_capacity", "refill_interval")

		//List of keys used in joinData. NEEDS TO BE IN THE SAME ORDER!
		keys = []string{"main", "client", "location", "container_model"}
	case "transaction":
		joinData["client"] = append(joinData["client"], "transaction", "client_id", "client_name")
		joinData["employee"] = append(joinData["employee"], "transaction", "employee_id", "employee_alias")
		joinData["location"] = append(joinData["location"], "transaction", "location_id", "location_name")
		joinData["container"] = append(joinData["container"], "transaction", "container_sr_number", "temp_id")
		joinData["container_model"] = append(joinData["container_model"], "container", "container_model_name", "liter_capacity")

		//List of keys used in joinData. NEEDS TO BE IN THE SAME ORDER!
		keys = []string{"main", "client", "employee", "location", "container", "container_model"}
	case "employee":
		joinData["location"] = append(joinData["location"], "employee", "location_id", "location_name")

		//List of keys used in joinData. NEEDS TO BE IN THE SAME ORDER!
		keys = []string{"main", "location"}
	}

	return joinData, keys
}
