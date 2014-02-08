/**
 * Copyright 2014 multibit.org
 *
 * Licensed under the MIT license (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://opensource.org/licenses/mit-license.php
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Based on the WalletProtobufSerialiser written by Miron Cuperman, copyright Google (also MIT licence)
 */

package org.multibit.hd.core.store;

import com.google.bitcoin.core.Sha256Hash;
import com.google.bitcoin.core.Transaction;
import com.google.common.collect.Sets;
import com.google.protobuf.ByteString;
import com.google.protobuf.TextFormat;
import org.multibit.contact.MBHDProtos;
import org.multibit.hd.core.api.Contact;
import org.multibit.hd.core.exceptions.ContactsLoadException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Set;

/**
 * Serialize and de-serialize contacts to a byte stream containing a
 * <a href="http://code.google.com/apis/protocolbuffers/docs/overview.html">protocol buffer</a>. Protocol buffers are
 * a data interchange format developed by Google with an efficient binary representation, a type safe specification
 * language and compilers that generate code to work with those data structures for many languages. Protocol buffers
 * can have their format evolved over time: conceptually they represent data using (tag, length, value) tuples. The
 * format is defined by the <tt>bitcoin.proto</tt> file in the bitcoinj source distribution.<p>
 *
 * This class is used through its static methods. The most common operations are writeContacts and readContacts, which do
 * the obvious operations on Output/InputStreams. You can use a {@link java.io.ByteArrayInputStream} and equivalent
 * {@link java.io.ByteArrayOutputStream} if you'd like byte arrays instead. The protocol buffer can also be manipulated
 * in its object form if you'd like to modify the flattened data structure before serialization to binary.<p>
 * 
 * @author Miron Cuperman
 * @author Jim Burton
 */
public class ContactsProtobufSerializer {
    private static final Logger log = LoggerFactory.getLogger(org.multibit.hd.core.store.ContactsProtobufSerializer.class);


    public ContactsProtobufSerializer() {
    }


    /**
     * Formats the given Contacts to the given output stream in protocol buffer format.<p>
     */
    public void writeContacts(Set<Contact> contacts, OutputStream output) throws IOException {
        MBHDProtos.Contacts contactsProto = contactsToProto(contacts);
        contactsProto.writeTo(output);
    }

    /**
     * Returns the given contacts formatted as text. The text format is that used by protocol buffers and although it
     * can also be parsed using {@link TextFormat#merge(CharSequence, com.google.protobuf.Message.Builder)},
     * it is designed more for debugging than storage. It is not well specified and wallets are largely binary data
     * structures anyway, consisting as they do of keys (large random numbers) and {@link Transaction}s which also
     * mostly contain keys and hashes.
     */
    public String contactsToText(Set<Contact> contacts) {
      MBHDProtos.Contacts contactsProto = contactsToProto(contacts);
        return TextFormat.printToString(contactsProto);
    }

    /**
     * Converts the given contacts to the object representation of the protocol buffers. This can be modified, or
     * additional data fields set, before serialization takes place.
     */
    public MBHDProtos.Contacts contactsToProto(Set<Contact> contacts) {
      MBHDProtos.Contacts.Builder contactsBuilder = MBHDProtos.Contacts.newBuilder();
//        walletBuilder.setNetworkIdentifier(wallet.getNetworkParameters().getId());
//        if (wallet.getDescription() != null) {
//            walletBuilder.setDescription(wallet.getDescription());
//        }
//
//        for (WalletTransaction wtx : wallet.getWalletTransactions()) {
//            Protos.Transaction txProto = makeTxProto(wtx);
//            walletBuilder.addTransaction(txProto);
//        }
//
//        for (ECKey key : wallet.getKeys()) {
//            Protos.Key.Builder keyBuilder = Protos.Key.newBuilder().setCreationTimestamp(key.getCreationTimeSeconds() * 1000)
//                                                         // .setLabel() TODO
//                                                            .setType(Protos.Key.Type.ORIGINAL);
//            if (key.getPrivKeyBytes() != null)
//                keyBuilder.setPrivateKey(ByteString.copyFrom(key.getPrivKeyBytes()));
//
//            EncryptedPrivateKey encryptedPrivateKey = key.getEncryptedPrivateKey();
//            if (encryptedPrivateKey != null) {
//                // Key is encrypted.
//                Protos.EncryptedPrivateKey.Builder encryptedKeyBuilder = Protos.EncryptedPrivateKey.newBuilder()
//                    .setEncryptedPrivateKey(ByteString.copyFrom(encryptedPrivateKey.getEncryptedBytes()))
//                    .setInitialisationVector(ByteString.copyFrom(encryptedPrivateKey.getInitialisationVector()));
//
//                if (key.getKeyCrypter() == null) {
//                    throw new IllegalStateException("The encrypted key " + key.toString() + " has no KeyCrypter.");
//                } else {
//                    // If it is a Scrypt + AES encrypted key, set the persisted key type.
//                    if (key.getKeyCrypter().getUnderstoodEncryptionType() == Protos.Wallet.EncryptionType.ENCRYPTED_SCRYPT_AES) {
//                        keyBuilder.setType(Protos.Key.Type.ENCRYPTED_SCRYPT_AES);
//                    } else {
//                        throw new IllegalArgumentException("The key " + key.toString() + " is encrypted with a KeyCrypter of type " + key.getKeyCrypter().getUnderstoodEncryptionType() +
//                                ". This WalletProtobufSerialiser does not understand that type of encryption.");
//                    }
//                }
//                keyBuilder.setEncryptedPrivateKey(encryptedKeyBuilder);
//            }
//
//            // We serialize the public key even if the private key is present for speed reasons: we don't want to do
//            // lots of slow EC math to load the wallet, we prefer to store the redundant data instead. It matters more
//            // on mobile platforms.
//            keyBuilder.setPublicKey(ByteString.copyFrom(key.getPubKey()));
//            walletBuilder.addKey(keyBuilder);
//        }
//
//        for (Script script : wallet.getWatchedScripts()) {
//            Protos.Script protoScript =
//                    Protos.Script.newBuilder()
//                            .setProgram(ByteString.copyFrom(script.getProgram()))
//                            .setCreationTimestamp(script.getCreationTimeSeconds() * 1000)
//                            .build();
//
//            walletBuilder.addWatchedScript(protoScript);
//        }
//
//        // Populate the lastSeenBlockHash field.
//        Sha256Hash lastSeenBlockHash = wallet.getLastBlockSeenHash();
//        if (lastSeenBlockHash != null) {
//            walletBuilder.setLastSeenBlockHash(hashToByteString(lastSeenBlockHash));
//            walletBuilder.setLastSeenBlockHeight(wallet.getLastBlockSeenHeight());
//        }
//        if (wallet.getLastBlockSeenTimeSecs() > 0)
//            walletBuilder.setLastSeenBlockTimeSecs(wallet.getLastBlockSeenTimeSecs());
//
//        // Populate the scrypt parameters.
//        KeyCrypter keyCrypter = wallet.getKeyCrypter();
//        if (keyCrypter == null) {
//            // The wallet is unencrypted.
//            walletBuilder.setEncryptionType(EncryptionType.UNENCRYPTED);
//        } else {
//            // The wallet is encrypted.
//            walletBuilder.setEncryptionType(keyCrypter.getUnderstoodEncryptionType());
//            if (keyCrypter instanceof KeyCrypterScrypt) {
//                KeyCrypterScrypt keyCrypterScrypt = (KeyCrypterScrypt) keyCrypter;
//                walletBuilder.setEncryptionParameters(keyCrypterScrypt.getScryptParameters());
//            } else {
//                // Some other form of encryption has been specified that we do not know how to persist.
//                throw new RuntimeException("The wallet has encryption of type '" + keyCrypter.getUnderstoodEncryptionType() + "' but this WalletProtobufSerializer does not know how to persist this.");
//            }
//        }
//
//        if (wallet.getKeyRotationTime() != null) {
//            long timeSecs = wallet.getKeyRotationTime().getTime() / 1000;
//            walletBuilder.setKeyRotationTime(timeSecs);
//        }
//
//        populateExtensions(wallet, walletBuilder);
//
//        // Populate the wallet version.
//        walletBuilder.setVersion(wallet.getVersion());

        return contactsBuilder.build();
    }

//    private static void populateExtensions(Wallet wallet, Protos.Wallet.Builder walletBuilder) {
//        for (WalletExtension extension : wallet.getExtensions().values()) {
//            Protos.Extension.Builder proto = Protos.Extension.newBuilder();
//            proto.setId(extension.getWalletExtensionID());
//            proto.setMandatory(extension.isWalletExtensionMandatory());
//            proto.setData(ByteString.copyFrom(extension.serializeWalletExtension()));
//            walletBuilder.addExtension(proto);
//        }
//    }
//
//    private static Protos.Transaction makeTxProto(WalletTransaction wtx) {
//        Transaction tx = wtx.getTransaction();
//        Protos.Transaction.Builder txBuilder = Protos.Transaction.newBuilder();
//
//        txBuilder.setPool(getProtoPool(wtx))
//                 .setHash(hashToByteString(tx.getHash()))
//                 .setVersion((int) tx.getVersion());
//
//        if (tx.getUpdateTime() != null) {
//            txBuilder.setUpdatedAt(tx.getUpdateTime().getTime());
//        }
//
//        if (tx.getLockTime() > 0) {
//            txBuilder.setLockTime((int)tx.getLockTime());
//        }
//
//        // Handle inputs.
//        for (TransactionInput input : tx.getInputs()) {
//            Protos.TransactionInput.Builder inputBuilder = Protos.TransactionInput.newBuilder()
//                .setScriptBytes(ByteString.copyFrom(input.getScriptBytes()))
//                .setTransactionOutPointHash(hashToByteString(input.getOutpoint().getHash()))
//                .setTransactionOutPointIndex((int) input.getOutpoint().getIndex());
//            if (input.hasSequence()) {
//                inputBuilder.setSequence((int)input.getSequenceNumber());
//            }
//            txBuilder.addTransactionInput(inputBuilder);
//        }
//
//        // Handle outputs.
//        for (TransactionOutput output : tx.getOutputs()) {
//            Protos.TransactionOutput.Builder outputBuilder = Protos.TransactionOutput.newBuilder()
//                .setScriptBytes(ByteString.copyFrom(output.getScriptBytes()))
//                .setValue(output.getValue().longValue());
//            final TransactionInput spentBy = output.getSpentBy();
//            if (spentBy != null) {
//                Sha256Hash spendingHash = spentBy.getParentTransaction().getHash();
//                int spentByTransactionIndex = spentBy.getParentTransaction().getInputs().indexOf(spentBy);
//                outputBuilder.setSpentByTransactionHash(hashToByteString(spendingHash))
//                             .setSpentByTransactionIndex(spentByTransactionIndex);
//            }
//            txBuilder.addTransactionOutput(outputBuilder);
//        }
//
//        // Handle which blocks tx was seen in.
//        final Map<Sha256Hash, Integer> appearsInHashes = tx.getAppearsInHashes();
//        if (appearsInHashes != null) {
//            for (Map.Entry<Sha256Hash, Integer> entry : appearsInHashes.entrySet()) {
//                txBuilder.addBlockHash(hashToByteString(entry.getKey()));
//                txBuilder.addBlockRelativityOffsets(entry.getValue());
//            }
//        }
//
//        if (tx.hasConfidence()) {
//            TransactionConfidence confidence = tx.getConfidence();
//            Protos.TransactionConfidence.Builder confidenceBuilder = Protos.TransactionConfidence.newBuilder();
//            writeConfidence(txBuilder, confidence, confidenceBuilder);
//        }
//
//        Protos.Transaction.Purpose purpose;
//        switch (tx.getPurpose()) {
//            case UNKNOWN: purpose = Protos.Transaction.Purpose.UNKNOWN; break;
//            case USER_PAYMENT: purpose = Protos.Transaction.Purpose.USER_PAYMENT; break;
//            case KEY_ROTATION: purpose = Protos.Transaction.Purpose.KEY_ROTATION; break;
//            default:
//                throw new RuntimeException("New tx purpose serialization not implemented.");
//        }
//        txBuilder.setPurpose(purpose);
//
//        return txBuilder.build();
//    }
//
//    private static Protos.Transaction.Pool getProtoPool(WalletTransaction wtx) {
//        switch (wtx.getPool()) {
//            case UNSPENT: return Protos.Transaction.Pool.UNSPENT;
//            case SPENT: return Protos.Transaction.Pool.SPENT;
//            case DEAD: return Protos.Transaction.Pool.DEAD;
//            case PENDING: return Protos.Transaction.Pool.PENDING;
//            default:
//                throw new RuntimeException("Unreachable");
//        }
//    }

//    private static void writeConfidence(Protos.Transaction.Builder txBuilder,
//                                        TransactionConfidence confidence,
//                                        Protos.TransactionConfidence.Builder confidenceBuilder) {
//        synchronized (confidence) {
//            confidenceBuilder.setType(Protos.TransactionConfidence.Type.valueOf(confidence.getConfidenceType().getValue()));
//            if (confidence.getConfidenceType() == ConfidenceType.BUILDING) {
//                confidenceBuilder.setAppearedAtHeight(confidence.getAppearedAtChainHeight());
//                confidenceBuilder.setDepth(confidence.getDepthInBlocks());
//                if (confidence.getWorkDone() != null) {
//                    confidenceBuilder.setWorkDone(confidence.getWorkDone().longValue());
//                }
//            }
//            if (confidence.getConfidenceType() == ConfidenceType.DEAD) {
//                // Copy in the overriding transaction, if available.
//                // (A dead coinbase transaction has no overriding transaction).
//                if (confidence.getOverridingTransaction() != null) {
//                    Sha256Hash overridingHash = confidence.getOverridingTransaction().getHash();
//                    confidenceBuilder.setOverridingTransaction(hashToByteString(overridingHash));
//                }
//            }
//            TransactionConfidence.Source source = confidence.getSource();
//            switch (source) {
//                case SELF: confidenceBuilder.setSource(Protos.TransactionConfidence.Source.SOURCE_SELF); break;
//                case NETWORK: confidenceBuilder.setSource(Protos.TransactionConfidence.Source.SOURCE_NETWORK); break;
//                case UNKNOWN:
//                    // Fall through.
//                default:
//                    confidenceBuilder.setSource(Protos.TransactionConfidence.Source.SOURCE_UNKNOWN); break;
//            }
//        }
//
//        for (ListIterator<PeerAddress> it = confidence.getBroadcastBy(); it.hasNext();) {
//            PeerAddress address = it.next();
//            Protos.PeerAddress proto = Protos.PeerAddress.newBuilder()
//                    .setIpAddress(ByteString.copyFrom(address.getAddr().getAddress()))
//                    .setPort(address.getPort())
//                    .setServices(address.getServices().longValue())
//                    .build();
//            confidenceBuilder.addBroadcastBy(proto);
//        }
//        txBuilder.setConfidence(confidenceBuilder);
//    }

    public static ByteString hashToByteString(Sha256Hash hash) {
        return ByteString.copyFrom(hash.getBytes());
    }

    public static Sha256Hash byteStringToHash(ByteString bs) {
        return new Sha256Hash(bs.toByteArray());
    }

    /**
     * <p>Parses a Contacts from the given stream, using the provided Contacts instance to load data into.
     * <p>A Contacts db can be unreadable for various reasons, such as inability to open the file, corrupt data, internally
     * inconsistent data, You should always
     * handle {@link org.multibit.hd.core.exceptions.ContactsLoadException} and communicate failure to the user in an appropriate manner.</p>
     *
     * @throws ContactsLoadException thrown in various error conditions (see description).
     */
    public Set<Contact> readContacts(InputStream input) throws ContactsLoadException {
        try {
            MBHDProtos.Contacts contactsProto = parseToProto(input);
            Set<Contact> contacts = Sets.newHashSet();
            readContacts(contactsProto, contacts);
            return contacts;
        } catch (IOException e) {
            throw new ContactsLoadException("Could not parse input stream to protobuf", e);
        }
    }

    /**
     * <p>Loads contacts data from the given protocol buffer and inserts it into the given Set of Contact object.
     *
     * <p>A contact db can be unreadable for various reasons, such as inability to open the file, corrupt data, internally
     * inconsistent data, a wallet extension marked as mandatory that cannot be handled and so on. You should always
     * handle {@link ContactsLoadException} and communicate failure to the user in an appropriate manner.</p>
     *
     * @throws ContactsLoadException thrown in various error conditions (see description).
     */
    private void readContacts(MBHDProtos.Contacts contactsProto, Set<Contact> contacts) throws ContactsLoadException {
//        // Read the scrypt parameters that specify how encryption and decryption is performed.
//        if (walletProto.hasEncryptionParameters()) {
//            Protos.ScryptParameters encryptionParameters = walletProto.getEncryptionParameters();
//            wallet.setKeyCrypter(new KeyCrypterScrypt(encryptionParameters));
//        }
//
//        if (walletProto.hasDescription()) {
//            wallet.setDescription(walletProto.getDescription());
//        }
//
//        // Read all keys
//        for (Protos.Key keyProto : walletProto.getKeyList()) {
//            if (!(keyProto.getType() == Protos.Key.Type.ORIGINAL || keyProto.getType() == Protos.Key.Type.ENCRYPTED_SCRYPT_AES)) {
//                throw new UnreadableWalletException("Unknown key type in wallet, type = " + keyProto.getType());
//            }
//
//            byte[] privKey = keyProto.hasPrivateKey() ? keyProto.getPrivateKey().toByteArray() : null;
//            EncryptedPrivateKey encryptedPrivateKey = null;
//            if (keyProto.hasEncryptedPrivateKey()) {
//                Protos.EncryptedPrivateKey encryptedPrivateKeyProto = keyProto.getEncryptedPrivateKey();
//                encryptedPrivateKey = new EncryptedPrivateKey(encryptedPrivateKeyProto.getInitialisationVector().toByteArray(),
//                        encryptedPrivateKeyProto.getEncryptedPrivateKey().toByteArray());
//            }
//
//            byte[] pubKey = keyProto.hasPublicKey() ? keyProto.getPublicKey().toByteArray() : null;
//
//            ECKey ecKey;
//            final KeyCrypter keyCrypter = wallet.getKeyCrypter();
//            if (keyCrypter != null && keyCrypter.getUnderstoodEncryptionType() != EncryptionType.UNENCRYPTED) {
//                // If the key is encrypted construct an ECKey using the encrypted private key bytes.
//                ecKey = new ECKey(encryptedPrivateKey, pubKey, keyCrypter);
//            } else {
//                // Construct an unencrypted private key.
//                ecKey = new ECKey(privKey, pubKey);
//            }
//            ecKey.setCreationTimeSeconds((keyProto.getCreationTimestamp() + 500) / 1000);
//            wallet.addKey(ecKey);
//        }
//
//        List<Script> scripts = Lists.newArrayList();
//        for (Protos.Script protoScript : walletProto.getWatchedScriptList()) {
//            try {
//                Script script =
//                        new Script(protoScript.getProgram().toByteArray(),
//                                protoScript.getCreationTimestamp() / 1000);
//                scripts.add(script);
//            } catch (ScriptException e) {
//                throw new UnreadableWalletException("Unparseable script in wallet");
//            }
//        }
//
//        wallet.addWatchedScripts(scripts);
//
//        // Read all transactions and insert into the txMap.
//        for (Protos.Transaction txProto : walletProto.getTransactionList()) {
//            readTransaction(txProto, wallet.getParams());
//        }
//
//        // Update transaction outputs to point to inputs that spend them
//        for (Protos.Transaction txProto : walletProto.getTransactionList()) {
//            WalletTransaction wtx = connectTransactionOutputs(txProto);
//            wallet.addWalletTransaction(wtx);
//        }
//
//        // Update the lastBlockSeenHash.
//        if (!walletProto.hasLastSeenBlockHash()) {
//            wallet.setLastBlockSeenHash(null);
//        } else {
//            wallet.setLastBlockSeenHash(byteStringToHash(walletProto.getLastSeenBlockHash()));
//        }
//        if (!walletProto.hasLastSeenBlockHeight()) {
//            wallet.setLastBlockSeenHeight(-1);
//        } else {
//            wallet.setLastBlockSeenHeight(walletProto.getLastSeenBlockHeight());
//        }
//        // Will default to zero if not present.
//        wallet.setLastBlockSeenTimeSecs(walletProto.getLastSeenBlockTimeSecs());
//
//        if (walletProto.hasKeyRotationTime()) {
//            wallet.setKeyRotationTime(new Date(walletProto.getKeyRotationTime() * 1000));
//        }
//
//        loadExtensions(wallet, walletProto);
//
//        if (walletProto.hasVersion()) {
//            wallet.setVersion(walletProto.getVersion());
//        }
//
//        // Make sure the object can be re-used to read another wallet without corruption.
//        txMap.clear();
    }

    /**
     * Returns the loaded protocol buffer from the given byte stream. This method is designed for low level work involving the
     * wallet file format itself.
     */
    public static MBHDProtos.Contacts parseToProto(InputStream input) throws IOException {
        return MBHDProtos.Contacts.parseFrom(input);
    }

//    private void readTransaction(Protos.Transaction txProto, NetworkParameters params) throws UnreadableWalletException {
//        Transaction tx = new Transaction(params);
//        if (txProto.hasUpdatedAt()) {
//            tx.setUpdateTime(new Date(txProto.getUpdatedAt()));
//        }
//
//        for (Protos.TransactionOutput outputProto : txProto.getTransactionOutputList()) {
//            BigInteger value = BigInteger.valueOf(outputProto.getValue());
//            byte[] scriptBytes = outputProto.getScriptBytes().toByteArray();
//            TransactionOutput output = new TransactionOutput(params, tx, value, scriptBytes);
//            tx.addOutput(output);
//        }
//
//        for (Protos.TransactionInput transactionInput : txProto.getTransactionInputList()) {
//            byte[] scriptBytes = transactionInput.getScriptBytes().toByteArray();
//            TransactionOutPoint outpoint = new TransactionOutPoint(params,
//                    transactionInput.getTransactionOutPointIndex() & 0xFFFFFFFFL,
//                    byteStringToHash(transactionInput.getTransactionOutPointHash())
//            );
//            TransactionInput input = new TransactionInput(params, tx, scriptBytes, outpoint);
//            if (transactionInput.hasSequence()) {
//                input.setSequenceNumber(transactionInput.getSequence());
//            }
//            tx.addInput(input);
//        }
//
//        for (int i = 0; i < txProto.getBlockHashCount(); i++) {
//            ByteString blockHash = txProto.getBlockHash(i);
//            int relativityOffset = 0;
//            if (txProto.getBlockRelativityOffsetsCount() > 0)
//                relativityOffset = txProto.getBlockRelativityOffsets(i);
//            tx.addBlockAppearance(byteStringToHash(blockHash), relativityOffset);
//        }
//
//        if (txProto.hasLockTime()) {
//            tx.setLockTime(0xffffffffL & txProto.getLockTime());
//        }
//
//        if (txProto.hasPurpose()) {
//            switch (txProto.getPurpose()) {
//                case UNKNOWN: tx.setPurpose(Transaction.Purpose.UNKNOWN); break;
//                case USER_PAYMENT: tx.setPurpose(Transaction.Purpose.USER_PAYMENT); break;
//                case KEY_ROTATION: tx.setPurpose(Transaction.Purpose.KEY_ROTATION); break;
//                default: throw new RuntimeException("New purpose serialization not implemented");
//            }
//        } else {
//            // Old wallet: assume a user payment as that's the only reason a new tx would have been created back then.
//            tx.setPurpose(Transaction.Purpose.USER_PAYMENT);
//        }
//
//        // Transaction should now be complete.
//        Sha256Hash protoHash = byteStringToHash(txProto.getHash());
//        if (!tx.getHash().equals(protoHash))
//            throw new UnreadableWalletException(String.format("Transaction did not deserialize completely: %s vs %s", tx.getHash(), protoHash));
//        if (txMap.containsKey(txProto.getHash()))
//            throw new UnreadableWalletException("Wallet contained duplicate transaction " + byteStringToHash(txProto.getHash()));
//        txMap.put(txProto.getHash(), tx);
//    }
//
//    private WalletTransaction connectTransactionOutputs(org.bitcoinj.wallet.Protos.Transaction txProto) throws UnreadableWalletException {
//        Transaction tx = txMap.get(txProto.getHash());
//        final WalletTransaction.Pool pool;
//        switch (txProto.getPool()) {
//            case DEAD: pool = WalletTransaction.Pool.DEAD; break;
//            case PENDING: pool = WalletTransaction.Pool.PENDING; break;
//            case SPENT: pool = WalletTransaction.Pool.SPENT; break;
//            case UNSPENT: pool = WalletTransaction.Pool.UNSPENT; break;
//            // Upgrade old wallets: inactive pool has been merged with the pending pool.
//            // Remove this some time after 0.9 is old and everyone has upgraded.
//            // There should not be any spent outputs in this tx as old wallets would not allow them to be spent
//            // in this state.
//            case INACTIVE:
//            case PENDING_INACTIVE:
//                pool = WalletTransaction.Pool.PENDING;
//                break;
//            default:
//                throw new UnreadableWalletException("Unknown transaction pool: " + txProto.getPool());
//        }
//        for (int i = 0 ; i < tx.getOutputs().size() ; i++) {
//            TransactionOutput output = tx.getOutputs().get(i);
//            final Protos.TransactionOutput transactionOutput = txProto.getTransactionOutput(i);
//            if (transactionOutput.hasSpentByTransactionHash()) {
//                final ByteString spentByTransactionHash = transactionOutput.getSpentByTransactionHash();
//                Transaction spendingTx = txMap.get(spentByTransactionHash);
//                if (spendingTx == null) {
//                    throw new UnreadableWalletException(String.format("Could not connect %s to %s",
//                            tx.getHashAsString(), byteStringToHash(spentByTransactionHash)));
//                }
//                final int spendingIndex = transactionOutput.getSpentByTransactionIndex();
//                TransactionInput input = checkNotNull(spendingTx.getInput(spendingIndex));
//                input.connect(output);
//            }
//        }
//
//        if (txProto.hasConfidence()) {
//            Protos.TransactionConfidence confidenceProto = txProto.getConfidence();
//            TransactionConfidence confidence = tx.getConfidence();
//            readConfidence(tx, confidenceProto, confidence);
//        }
//
//        return new WalletTransaction(pool, tx);
//    }
//
//    private void readConfidence(Transaction tx, Protos.TransactionConfidence confidenceProto,
//                                TransactionConfidence confidence) throws UnreadableWalletException {
//        // We are lenient here because tx confidence is not an essential part of the wallet.
//        // If the tx has an unknown type of confidence, ignore.
//        if (!confidenceProto.hasType()) {
//            log.warn("Unknown confidence type for tx {}", tx.getHashAsString());
//            return;
//        }
//        ConfidenceType confidenceType;
//        switch (confidenceProto.getType()) {
//            case BUILDING: confidenceType = ConfidenceType.BUILDING; break;
//            case DEAD: confidenceType = ConfidenceType.DEAD; break;
//            // These two are equivalent (must be able to read old wallets).
//            case NOT_IN_BEST_CHAIN: confidenceType = ConfidenceType.PENDING; break;
//            case PENDING: confidenceType = ConfidenceType.PENDING; break;
//            case UNKNOWN:
//                // Fall through.
//            default:
//                confidenceType = ConfidenceType.UNKNOWN; break;
//        }
//        confidence.setConfidenceType(confidenceType);
//        if (confidenceProto.hasAppearedAtHeight()) {
//            if (confidence.getConfidenceType() != ConfidenceType.BUILDING) {
//                log.warn("Have appearedAtHeight but not BUILDING for tx {}", tx.getHashAsString());
//                return;
//            }
//            confidence.setAppearedAtChainHeight(confidenceProto.getAppearedAtHeight());
//        }
//        if (confidenceProto.hasDepth()) {
//            if (confidence.getConfidenceType() != ConfidenceType.BUILDING) {
//                log.warn("Have depth but not BUILDING for tx {}", tx.getHashAsString());
//                return;
//            }
//            confidence.setDepthInBlocks(confidenceProto.getDepth());
//        }
//        if (confidenceProto.hasWorkDone()) {
//            if (confidence.getConfidenceType() != ConfidenceType.BUILDING) {
//                log.warn("Have workDone but not BUILDING for tx {}", tx.getHashAsString());
//                return;
//            }
//            confidence.setWorkDone(BigInteger.valueOf(confidenceProto.getWorkDone()));
//        }
//        if (confidenceProto.hasOverridingTransaction()) {
//            if (confidence.getConfidenceType() != ConfidenceType.DEAD) {
//                log.warn("Have overridingTransaction but not OVERRIDDEN for tx {}", tx.getHashAsString());
//                return;
//            }
//            Transaction overridingTransaction =
//                txMap.get(confidenceProto.getOverridingTransaction());
//            if (overridingTransaction == null) {
//                log.warn("Have overridingTransaction that is not in wallet for tx {}", tx.getHashAsString());
//                return;
//            }
//            confidence.setOverridingTransaction(overridingTransaction);
//        }
//        for (Protos.PeerAddress proto : confidenceProto.getBroadcastByList()) {
//            InetAddress ip;
//            try {
//                ip = InetAddress.getByAddress(proto.getIpAddress().toByteArray());
//            } catch (UnknownHostException e) {
//                throw new UnreadableWalletException("Peer IP address does not have the right length", e);
//            }
//            int port = proto.getPort();
//            PeerAddress address = new PeerAddress(ip, port);
//            address.setServices(BigInteger.valueOf(proto.getServices()));
//            confidence.markBroadcastBy(address);
//        }
//        switch (confidenceProto.getSource()) {
//            case SOURCE_SELF: confidence.setSource(TransactionConfidence.Source.SELF); break;
//            case SOURCE_NETWORK: confidence.setSource(TransactionConfidence.Source.NETWORK); break;
//            case SOURCE_UNKNOWN:
//                // Fall through.
//            default: confidence.setSource(TransactionConfidence.Source.UNKNOWN); break;
//        }
//    }
}
