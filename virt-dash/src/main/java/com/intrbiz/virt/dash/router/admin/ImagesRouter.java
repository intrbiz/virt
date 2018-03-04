package com.intrbiz.virt.dash.router.admin;

import java.io.IOException;
import java.util.UUID;
import java.util.stream.Collectors;

import com.intrbiz.balsa.engine.route.Router;
import com.intrbiz.balsa.error.BalsaConversionError;
import com.intrbiz.balsa.error.BalsaValidationError;
import com.intrbiz.balsa.metadata.WithDataAdapter;
import com.intrbiz.metadata.Any;
import com.intrbiz.metadata.Catch;
import com.intrbiz.metadata.CheckStringLength;
import com.intrbiz.metadata.Get;
import com.intrbiz.metadata.IsaLong;
import com.intrbiz.metadata.IsaUUID;
import com.intrbiz.metadata.Param;
import com.intrbiz.metadata.Post;
import com.intrbiz.metadata.Prefix;
import com.intrbiz.metadata.RequirePermission;
import com.intrbiz.metadata.RequireValidPrincipal;
import com.intrbiz.metadata.Template;
import com.intrbiz.virt.dash.App;
import com.intrbiz.virt.data.VirtDB;
import com.intrbiz.virt.model.Image;

@Prefix("/admin/image")
@Template("layout/main")
@RequireValidPrincipal()
@RequirePermission("global_admin")
public class ImagesRouter extends Router<App>
{
    @Any("/")
    @WithDataAdapter(VirtDB.class)
    public void images(VirtDB db)
    {
        var("images", db.listImages().stream().filter((i) -> i.isOpen()).collect(Collectors.toList()));
        encode("admin/images");
    }
    
    @Get("/new")
    public void newImage()
    {
        encode("admin/new-image");
    }
    
    @Post("/new")
    @WithDataAdapter(VirtDB.class)
    public void newImage(
            VirtDB db,
            @Param("name") @CheckStringLength(mandatory=true, min=3) String name,
            @Param("size") @IsaLong long size,
            @Param("source") @CheckStringLength() String source,
            @Param("provider") @CheckStringLength() String provider,
            @Param("vendor") @CheckStringLength() String vendor,
            @Param("product") @CheckStringLength() String product,
            @Param("description") @CheckStringLength() String description
    ) throws IOException
    {
        Image image = new Image(name, size, source);
        image.setProvider(provider);
        image.setVendor(vendor);
        image.setProduct(product);
        image.setDescription(description);
        image.setOpen(true);
        db.setImage(image);
        redirect("/admin/image/");
    }
    
    @Catch({BalsaConversionError.class, BalsaValidationError.class})
    @Post("/new")
    public void newImageErrors()
    {
        encode("admin/new-image");
    }
    
    @Any("/id/:id/remove")
    @WithDataAdapter(VirtDB.class)
    public void removeZone(VirtDB db, @IsaUUID() UUID id) throws IOException
    {
        db.removeImage(id);
        redirect("/admin/image/");
    }
}
