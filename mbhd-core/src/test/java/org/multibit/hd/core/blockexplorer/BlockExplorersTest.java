package org.multibit.hd.core.blockexplorer;

import com.google.common.base.Optional;
import org.junit.Test;

import java.text.MessageFormat;
import java.util.Collection;
import java.util.Iterator;

import static org.fest.assertions.Assertions.assertThat;

public class BlockExplorersTest {

  /**
   * A vanilla example tx id to lookup
   */
  private static final String[] EXAMPLE_TX_ID = new String[]{"58be29550b129655bebae4ceb948768278504fe1378f7a624ab5907019d167da"};

  private static final String EXPECTED_BIT_EASY_COM_URL = "https://www.biteasy.com/blockchain/transactions/58be29550b129655bebae4ceb948768278504fe1378f7a624ab5907019d167da";
  private static final String EXPECTED_BLOCKCHAIN_INFO_URL = "https://blockchain.info/tx-index/58be29550b129655bebae4ceb948768278504fe1378f7a624ab5907019d167da";
  private static final String EXPECTED_BLOCKR_IO_URL = "http://btc.blockr.io/tx/info/58be29550b129655bebae4ceb948768278504fe1378f7a624ab5907019d167da";
  private static final String EXPECTED_BLOCK_TRAIL_COM_URL = "https://www.blocktrail.com/BTC/tx/58be29550b129655bebae4ceb948768278504fe1378f7a624ab5907019d167da";


  @Test
  public void testDefaultBlockExplorer() {
    BlockExplorer defaultBlockExplorer = BlockExplorers.getDefaultBlockExplorer();
    assertThat(defaultBlockExplorer instanceof BlockChainInfoBlockExplorer).isTrue();
  }

  @Test
  public void testGetAll() {
    Collection<BlockExplorer> allBlockExplorers = BlockExplorers.getAll();

    Iterator<BlockExplorer> iterator = allBlockExplorers.iterator();

    assertThat(iterator.next() instanceof BitEasyComBlockExplorer).isTrue();
    assertThat(iterator.next() instanceof BlockChainInfoBlockExplorer).isTrue();
    assertThat(iterator.next() instanceof BlockrIoBlockExplorer).isTrue();
    assertThat(iterator.next() instanceof BlockTrailComBlockExplorer).isTrue();

    // No more
    assertThat(iterator.hasNext()).isFalse();
  }

  @Test
  public void testBitEasyCom() {
    final String expectedId = "biteasy";
    assertThat(expectedId.equals(BitEasyComBlockExplorer.ID));

    Optional<BlockExplorer> blockExplorerOptional = BlockExplorers.getBlockExplorerById(BitEasyComBlockExplorer.ID);
    assertThat(blockExplorerOptional.isPresent()).isTrue();
    BlockExplorer blockExplorer = blockExplorerOptional.get();

    assertThat(blockExplorer instanceof BitEasyComBlockExplorer).isTrue();
    assertThat(blockExplorer.getName().equals("biteasy.com")).isTrue();

    MessageFormat format = blockExplorer.getTransactionLookupMessageFormat();
    assertThat(EXPECTED_BIT_EASY_COM_URL.equals(format.format(EXAMPLE_TX_ID))).isTrue();
  }

  @Test
  public void testBlockChainInfo() {
    final String expectedId = "blockchain";
    assertThat(expectedId.equals(BlockChainInfoBlockExplorer.ID));

    Optional<BlockExplorer> blockExplorerOptional = BlockExplorers.getBlockExplorerById(BlockChainInfoBlockExplorer.ID);
    assertThat(blockExplorerOptional.isPresent()).isTrue();
    BlockExplorer blockExplorer = blockExplorerOptional.get();

    assertThat(blockExplorer instanceof BlockChainInfoBlockExplorer).isTrue();
    assertThat(blockExplorer.getName().equals("blockchain.info")).isTrue();

    MessageFormat format = blockExplorer.getTransactionLookupMessageFormat();
    assertThat(EXPECTED_BLOCKCHAIN_INFO_URL.equals(format.format(EXAMPLE_TX_ID))).isTrue();
  }
  @Test
  public void testBlockrIo() {
    final String expectedId = "blockr";
    assertThat(expectedId.equals(BlockrIoBlockExplorer.ID));

    Optional<BlockExplorer> blockExplorerOptional = BlockExplorers.getBlockExplorerById(BlockrIoBlockExplorer.ID);
    assertThat(blockExplorerOptional.isPresent()).isTrue();
    BlockExplorer blockExplorer = blockExplorerOptional.get();

    assertThat(blockExplorer instanceof BlockrIoBlockExplorer).isTrue();
    assertThat(blockExplorer.getName().equals("blockr.io")).isTrue();

    MessageFormat format = blockExplorer.getTransactionLookupMessageFormat();
    assertThat(EXPECTED_BLOCKR_IO_URL.equals(format.format(EXAMPLE_TX_ID))).isTrue();
  }

  @Test
   public void testBlockTrailCom() {
     final String expectedId = "blocktrail";
     assertThat(expectedId.equals(BlockTrailComBlockExplorer.ID));

     Optional<BlockExplorer> blockExplorerOptional = BlockExplorers.getBlockExplorerById(BlockTrailComBlockExplorer.ID);
     assertThat(blockExplorerOptional.isPresent()).isTrue();
     BlockExplorer blockExplorer = blockExplorerOptional.get();

     assertThat(blockExplorer instanceof BlockTrailComBlockExplorer).isTrue();
     assertThat(blockExplorer.getName().equals("blocktrail.com")).isTrue();

     MessageFormat format = blockExplorer.getTransactionLookupMessageFormat();
     assertThat(EXPECTED_BLOCK_TRAIL_COM_URL.equals(format.format(EXAMPLE_TX_ID))).isTrue();
   }
}
