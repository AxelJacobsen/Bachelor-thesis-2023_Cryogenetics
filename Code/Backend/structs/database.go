package structs

/**
 *	Initial Tank struct
 */
type Tank struct {
	ID         string  `json:"id"`
	LastFilled uint8   `json:"status"`
	Location   string  `json:"longitude"`
	Latitude   float64 `json:"latitude"`
}
