package globals

import (
	"context"
	"database/sql"
	"time"
)

// Session timer
var StartTime time.Time

// Context
var Ctx context.Context

// Database
var DB *sql.DB
