package globals_test

import (
	"backend/constants"
	"backend/globals"
	"fmt"
	"net/http"
	"net/url"
	"reflect"
	"testing"
)

/**
 *	Parses an url, returning nil if the parse fails.
 *	(This is just a shorthand for parsing when the error message is irrelevant)
 *
 *	@param rawURL - The raw URL string.
 *
 *	@return The parsed URL.
 */
func parseAndVerifyUrl(rawURL string) *url.URL {
	u, err := url.Parse(rawURL)
	if err != nil {
		return nil
	}
	return u
}

/**
 *	Tester for the ConvertUrlToSql function.
 */
func TestConvertUrlToSql(t *testing.T) {
	activeTable := "transaction"
	jd := make(map[string][]string)
	jd["main"] = append(jd["main"], activeTable, "", "*") //WHAT TABLE IS THE SQL REQUEST FOR?

	var k []string
	k = []string{"main"}

	jd, k = constants.SetJoinData(jd, k, activeTable)

	// Set up subtests
	subtests := []struct {
		name             string
		r                *http.Request
		joinData         map[string][]string
		keys             []string
		expectedSqlQuery string
		expectedSqlArgs  []interface{}
		expectedErr      error
	}{
		// GET
		{ // The "normal" path.
			name: "normal",
			r: &http.Request{
				Method: "GET",
				URL:    parseAndVerifyUrl("localhost:8080/api/transaction"),
			},
			joinData:         jd,
			keys:             k,
			expectedSqlQuery: "SELECT transaction.*, client.client_name, employee.employee_alias, location.location_name, container.temp_id, container_model.liter_capacity FROM transaction LEFT JOIN client ON transaction.client_id = client.client_id LEFT JOIN employee ON transaction.employee_id = employee.employee_id LEFT JOIN location ON transaction.location_id = location.location_id LEFT JOIN container ON transaction.container_sr_number = container.container_sr_number LEFT JOIN container_model ON container.container_model_name = container_model.container_model_name  ",
			expectedSqlArgs:  []interface{}{},
			expectedErr:      nil,
		},
		{ // A few, valid queries.
			name: "valid queries",
			r: &http.Request{
				Method: "GET",
				URL:    parseAndVerifyUrl("localhost:8080/api/transaction?liter_capacity=20&temp_id=12"),
			},
			joinData:         jd,
			keys:             k,
			expectedSqlQuery: "SELECT transaction.*, client.client_name, employee.employee_alias, location.location_name, container.temp_id, container_model.liter_capacity FROM transaction LEFT JOIN client ON transaction.client_id = client.client_id LEFT JOIN employee ON transaction.employee_id = employee.employee_id LEFT JOIN location ON transaction.location_id = location.location_id LEFT JOIN container ON transaction.container_sr_number = container.container_sr_number LEFT JOIN container_model ON container.container_model_name = container_model.container_model_name   WHERE  container_model.liter_capacity = ? OR container.temp_id = ?",
			expectedSqlArgs: []interface{}{
				12,
				20,
			},
			expectedErr: nil,
		},
		{ // Queries of different types.
			name: "queries of different types",
			r: &http.Request{
				Method: "GET",
				URL:    parseAndVerifyUrl("localhost:8080/api/transaction?address=Test&temp_id=3"),
			},
			joinData:         jd,
			keys:             k,
			expectedSqlQuery: "SELECT transaction.*, client.client_name, employee.employee_alias, location.location_name, container.temp_id, container_model.liter_capacity FROM transaction LEFT JOIN client ON transaction.client_id = client.client_id LEFT JOIN employee ON transaction.employee_id = employee.employee_id LEFT JOIN location ON transaction.location_id = location.location_id LEFT JOIN container ON transaction.container_sr_number = container.container_sr_number LEFT JOIN container_model ON container.container_model_name = container_model.container_model_name   WHERE  transaction.address = ? OR container.temp_id = ?",
			expectedSqlArgs: []interface{}{
				3,
				"Test",
			},
			expectedErr: nil,
		},
	}

	// Test subtests
	for _, subtest := range subtests {
		t.Run(subtest.name, func(t *testing.T) {
			// Run function
			sqlQuery, sqlArgs, err := globals.ConvertUrlToSql(
				subtest.r,
				subtest.joinData,
				subtest.keys,
			)

			// Verify error
			if err != subtest.expectedErr {
				t.Errorf("Expected error '%v' but got '%v'!", subtest.expectedErr, err)
			}

			// Verify query
			if sqlQuery != subtest.expectedSqlQuery {
				t.Errorf("Expected '%s' but got '%s'!", subtest.expectedSqlQuery, sqlQuery)
			}

			// Verify args length
			if len(sqlArgs) != len(subtest.expectedSqlArgs) {
				t.Errorf("Expected slice '%v' had different length than given slice '%v'!", subtest.expectedSqlArgs, sqlArgs)
			} else {
				// Verify args values
				expectedSqlArgsC := make([]interface{}, len(subtest.expectedSqlArgs))
				copy(expectedSqlArgsC, subtest.expectedSqlArgs) // Make a temporary copy which elements are removed from once found
				for _, sqlArg := range sqlArgs {
					found := false
					for j, expectedSqlArgC := range expectedSqlArgsC {
						if reflect.DeepEqual(sqlArg, expectedSqlArgC) || reflect.DeepEqual(sqlArg, fmt.Sprintf("%v", expectedSqlArgC)) {
							expectedSqlArgsC = append(expectedSqlArgsC[:j], expectedSqlArgsC[j+1:]...)
							found = true
							break
						}
					}

					if !found {
						t.Errorf("Given value '%v' was not found in expected value(s) '%v'!", sqlArg, subtest.expectedSqlArgs)
					}
				}

				if len(expectedSqlArgsC) > 0 {
					t.Errorf("Expected value(s) '%v' were not found in given value(s) '%v'", expectedSqlArgsC, sqlArgs)
				}
			}
		})
	}
}
