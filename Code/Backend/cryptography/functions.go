package cryptography

import (
	"backend/constants"
	"backend/request"
	"crypto/rand"
	"crypto/rsa"
	"crypto/x509"
	"database/sql"
	"encoding/base64"
	"errors"
	"io"
	"math/big"
	"net/http/httptest"
	"os"
	"strconv"
	"strings"
)

const KEY_BITS = 4096

/**
 *	Fetches the private key, generating one if none is found.
 */
func FetchPrivateKey() (*rsa.PrivateKey, error) {
	// If key file already exists, attempt fetching its key
	if _, err := os.Stat(constants.KEY_FILEPATH); err == nil {
		// Open the keyfile
		bytes, err := os.ReadFile(constants.KEY_FILEPATH)
		if err != nil {
			return nil, errors.New("error reading key file")
		}

		// Parse the key
		key, err := x509.ParsePKCS1PrivateKey(bytes)
		if err != nil {
			return nil, errors.New("error parsing key file")
		}

		return key, nil

		// If the key file doesn't exist, create it and generate a key
	} else {
		// Create the keyfile
		file, err := os.Create(constants.KEY_FILEPATH)
		if err != nil {
			return nil, errors.New("error creating key file")
		}
		defer file.Close()

		// Generate the key
		key, err := generatePrivateKey(KEY_BITS, rand.Reader)
		if err != nil {
			return nil, errors.New("error generating key")
		}

		// Write marshaled key to file
		keyMarshalled := x509.MarshalPKCS1PrivateKey(key)
		_, err = file.Write(keyMarshalled)
		if err != nil {
			return nil, errors.New("error writing to key file")
		}

		return key, nil
	}
}

/**
 *	Generates a private key.
 *
 *	@param bits - Size of the key in bits.
 *	@param seedgen - Random number generator.
 *
 *	@return The private key and an error if something went wrong.
 */
func generatePrivateKey(bits int, seedgen io.Reader) (*rsa.PrivateKey, error) {
	// Generate private key, returning if an error occured
	privateKey, err := rsa.GenerateKey(seedgen, bits)
	if err != nil {
		return nil, err
	}

	// Validate the private key, returning if it was invalid
	err = privateKey.Validate()
	if err != nil {
		return nil, err
	}

	// If nothing went wrong, return the private key
	return privateKey, nil
}

/**
 *	Encrypts a set of bytes using a given public key.
 *
 *	@param bytes - The bytes to encrypt.
 *	@param publicKey - The key to encrypt the bytes with.
 *
 *	@return The encrypted bytes and an error if something went wrong.
 */
func Encrypt(bytes []byte, publicKey *rsa.PublicKey) ([]byte, error) {
	// Encrypt
	//hash := sha512.New()
	//cipher, err := rsa.EncryptOAEP(hash, rand.Reader, publicKey, bytes, nil)
	cipher, err := rsa.EncryptPKCS1v15(rand.Reader, publicKey, bytes)
	if err != nil {
		return nil, err
	}

	return cipher, nil
}

/**
 *	Decrypts a set of bytes.
 *
 *	@param bytes - The bytes to decrypt.
 *	@param privateKey - The private key to decrypt the bytes with. If none is given, uses ours.
 *
 *	@return The decrypted bytes and an error if something went wrong.
 */
func Decrypt(bytes []byte, privateKey ...*rsa.PrivateKey) ([]byte, error) {
	var key *rsa.PrivateKey

	// If no key is given, check for a stored one
	if len(privateKey) == 0 {
		fetched, err := FetchPrivateKey()
		if err != nil {
			return nil, err
		}
		key = fetched
	} else {
		// Otherwise, pick the first one supplied
		key = privateKey[0]
	}

	// Decrypt
	//hash := sha512.New()
	//deciphered, err := rsa.DecryptOAEP(hash, rand.Reader, key, bytes, nil)
	deciphered, err := rsa.DecryptPKCS1v15(rand.Reader, key, bytes)
	if err != nil {
		return nil, err
	}

	return deciphered, nil
}

/**
 *	Encodes a set of bytes using base64.
 *	"\n" and "+" are replaced.
 *
 *	@param data - The data to encode.
 *
 *	@return The encoded data.
 */
func EncodeBase64(data []byte) string {
	encoded := base64.URLEncoding.EncodeToString(data)
	encoded_fixed := strings.ReplaceAll(encoded, "+", "{plus}")
	encoded_fixed = strings.ReplaceAll(encoded_fixed, "\n", "{newline}")
	return encoded_fixed
}

/**
 *	Decodes a string using base64.
 *
 *	@param encoded - The encoded string.
 *
 *	@return The decoded string, and an error if something went wrong.
 */
func DecodeBase64(encoded string) ([]byte, error) {
	encoded_fixed := strings.ReplaceAll(encoded, "{plus}", "+")
	encoded_fixed = strings.ReplaceAll(encoded_fixed, "{newline}", "\n")
	return base64.URLEncoding.DecodeString(encoded_fixed)
}

/**
 *	Verifies an unique number against the database.
 *
 *	@param db - The database.
 *	@param uniqueNumber - The unique number to check.
 *
 *	@return true if the unique number was found in the database.
 */
func VerifyUniqueNumber(db *sql.DB, uniqueNumber string) bool {
	// Fetch matching uniqueNumbers from DB
	w := httptest.NewRecorder()
	data, err := request.QueryJSON(db, "SELECT * FROM `valid_keys` WHERE keyvalue = ?", []interface{}{uniqueNumber}, w)
	if err != nil {
		return false
	}

	// If there are more than one matching entries, return true
	return len(data) > 0
}

/**
 *	Verifies an unique number against the database.
 *
 *	@param db - The database.
 *	@param uniqueNumber - The unique number to check.
 *
 *	@return true if the unique number was found in the database.
 */
func RequestVerification(db *sql.DB, uniqueNumber string) error {
	// Fetch matching uniqueNumbers from DB
	w := httptest.NewRecorder()
	_, err := request.QueryJSON(db, "INSERT INTO `requested_keys` (`keyvalue`) VALUES (?)", []interface{}{uniqueNumber}, w)
	if err != nil {
		return err
	}
	return nil
}

/**
 *	Encrypts and encodes a set of bytes using a public key E and N value.
 *
 *	@param publicKeyE - The public exponent of the public key.
 *	@param publicKeyN - The modulus of the public key.
 *
 *	@return The encrypted and encoded string, or an error if something went wrong.
 */
func EncryptAndEncode(publicKeyE string, publicKeyN string, bytes []byte) (string, error) {
	// Convert strings to (big)ints
	publicKeyEVal, err := strconv.Atoi(publicKeyE)
	if err != nil {
		return "", err
	}
	publicKeyNVal := new(big.Int)
	publicKeyNVal.SetString(publicKeyN, 10)

	res, err := Encrypt(
		bytes,
		&rsa.PublicKey{N: publicKeyNVal, E: publicKeyEVal},
	)
	if err != nil {
		return "", err
	}

	return EncodeBase64(res), nil
}
