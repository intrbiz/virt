package com.intrbiz.virt.model;

import java.util.UUID;

import com.intrbiz.data.db.compiler.meta.Action;
import com.intrbiz.data.db.compiler.meta.SQLColumn;
import com.intrbiz.data.db.compiler.meta.SQLForeignKey;
import com.intrbiz.data.db.compiler.meta.SQLPrimaryKey;
import com.intrbiz.data.db.compiler.meta.SQLTable;
import com.intrbiz.data.db.compiler.meta.SQLVersion;
import com.intrbiz.virt.data.VirtDB;

@SQLTable(schema = VirtDB.class, name = "user_account_grant", since = @SQLVersion({ 1, 0, 0 }))
public class UserAccountGrant
{
    @SQLColumn(index = 1, name = "user_id", since = @SQLVersion({ 1, 0, 0 }))
    @SQLPrimaryKey()
    @SQLForeignKey(references = User.class, on = "id", onDelete = Action.CASCADE, onUpdate = Action.CASCADE, since = @SQLVersion({ 1, 0, 0 }) )
    private UUID userId;

    @SQLColumn(index = 2, name = "account_id", notNull = true, since = @SQLVersion({ 1, 0, 0 }) )
    @SQLPrimaryKey()
    @SQLForeignKey(references = Account.class, on = "id", onDelete = Action.CASCADE, onUpdate = Action.CASCADE, since = @SQLVersion({ 1, 0, 0 }) )
    private UUID accountId;

    @SQLColumn(index = 3, name = "role", since = @SQLVersion({ 1, 0, 0 }))
    private Role role;

    public UserAccountGrant()
    {
        super();
    }

    public UserAccountGrant(UUID userId, UUID accountId, Role role)
    {
        super();
        this.userId = userId;
        this.accountId = accountId;
        this.role = role;
    }

    public UUID getUserId()
    {
        return userId;
    }

    public void setUserId(UUID userId)
    {
        this.userId = userId;
    }

    public UUID getAccountId()
    {
        return accountId;
    }

    public void setAccountId(UUID accountId)
    {
        this.accountId = accountId;
    }

    public Role getRole()
    {
        return role;
    }

    public void setRole(Role role)
    {
        this.role = role;
    }
    
    public boolean hasPermission(Permission permission)
    {
        if (this.role != null)
            return this.role.hasPermission(permission);
        return false;
    }
    
    public Account getAccount()
    {
        try (VirtDB db = VirtDB.connect())
        {
            return db.getAccount(this.accountId);
        }
    }

    @Override
    public int hashCode()
    {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((accountId == null) ? 0 : accountId.hashCode());
        result = prime * result + ((userId == null) ? 0 : userId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj) return true;
        if (obj == null) return false;
        if (getClass() != obj.getClass()) return false;
        UserAccountGrant other = (UserAccountGrant) obj;
        if (accountId == null)
        {
            if (other.accountId != null) return false;
        }
        else if (!accountId.equals(other.accountId)) return false;
        if (userId == null)
        {
            if (other.userId != null) return false;
        }
        else if (!userId.equals(other.userId)) return false;
        return true;
    }
}
