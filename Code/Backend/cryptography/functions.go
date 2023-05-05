package cryptography

import (
	"backend/constants"
	"crypto/rand"
	"crypto/rsa"
	"crypto/sha512"
	"crypto/x509"
	"errors"
	"io"
	"os"
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
	hash := sha512.New()
	cipher, err := rsa.EncryptOAEP(hash, rand.Reader, publicKey, bytes, nil)
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
	hash := sha512.New()
	deciphered, err := rsa.DecryptOAEP(hash, rand.Reader, key, bytes, nil)
	if err != nil {
		return nil, err
	}

	return deciphered, nil
}
