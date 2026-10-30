package saifi.net.transactionservice.mcp;

@Configuration
public class McpConfig {
    @Bean
    public ToolCallbackProvider transactionTools(TransactionMcpTools tools) {
        return MethodToolCallbackProvider.builder().toolObjects(tools).build();
    }
}
