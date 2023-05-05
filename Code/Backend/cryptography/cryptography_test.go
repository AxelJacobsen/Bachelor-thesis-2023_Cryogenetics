package cryptography_test

import (
	"io"
	"strings"
	"testing"
)

/**
 *	Tester for the ConvertUrlToSql function.
 */
func TestGeneratePrivateKey(t *testing.T) {
	// Set up subtests
	subtests := []struct {
		name    string
		bits    int
		seedgen io.Reader
	}{
		// GET
		{ // The "normal" path.
			name:    "normal",
			bits:    1024,
			seedgen: strings.NewReader("1542208053"),
		},
	}

	// Test subtests
	for _, subtest := range subtests {
		t.Run(subtest.name, func(t *testing.T) {

		})
	}
}
